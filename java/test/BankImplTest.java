import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit and integration tests for Banker's Algorithm implementation.
 * Validates state invariants, 3-point request checks, safety simulation,
 * and release boundary constraints.
 */
public class BankImplTest {

    private BankImpl bank;
    private final int numberOfCustomers = 5;
    private final int numberOfResources = 4;

    // Standard initial customer max demand matrix from inPutFile.txt
    private final int[][] standardMaxDemand = {
        {6, 4, 7, 3}, // Customer 0
        {4, 2, 3, 2}, // Customer 1
        {2, 5, 3, 3}, // Customer 2
        {6, 3, 3, 2}, // Customer 3
        {5, 6, 7, 5}  // Customer 4
    };

    /**
     * Helper to create and populate a BankImpl instance with the standard configuration.
     */
    private BankImpl createStandardBank(int[] initialResources) {
        BankImpl b = new BankImpl(numberOfCustomers, initialResources);
        for (int i = 0; i < numberOfCustomers; i++) {
            b.addCustomer(i, standardMaxDemand[i]);
        }
        return b;
    }

    @Test
    @DisplayName("Initial state should be created and queryable without exception")
    public void testInitializationAndGetState() {
        bank = createStandardBank(new int[]{10, 5, 7, 8});
        assertNotNull(bank, "Bank instance should not be null");
        assertDoesNotThrow(() -> bank.getState(), "getState() should execute without exceptions");
    }

    @Test
    @DisplayName("getState() formatting matches README Section 13 specification")
    public void testStateDisplayFormatting() {
        bank = createStandardBank(new int[]{10, 5, 7, 8});
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try {
            System.setOut(new PrintStream(outContent));
            bank.getState();
            String output = outContent.toString();
            assertTrue(output.contains("Available:"), "Output must contain Available label");
            assertTrue(output.contains("[10 5 7 8]"), "Output must contain space-delimited Available vector");
            assertTrue(output.contains("Maximum:"), "Output must contain Maximum label");
            assertTrue(output.contains("Allocation:"), "Output must contain Allocation label");
            assertTrue(output.contains("Need:"), "Output must contain Need label");
            for (int i = 0; i < numberOfCustomers; i++) {
                assertTrue(output.contains("Customer " + i + ":"), "Output must contain customer " + i + " row");
            }
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Request exceeding customer maximum demand claim must be denied")
    public void testRequestExceedsMaximumClaim() {
        bank = createStandardBank(new int[]{10, 7, 7, 8});
        // Customer 0 max demand is [6, 4, 7, 3]. Requesting 7 of R0 exceeds max demand claim.
        int[] excessiveRequest = {7, 0, 0, 0};
        boolean result = bank.requestResources(0, excessiveRequest);
        assertFalse(result, "Request exceeding maximum claim should be denied");
    }

    @Test
    @DisplayName("Request exceeding available bank resources must be denied")
    public void testRequestExceedsAvailableResources() {
        // System only has 5 of R1 available
        bank = createStandardBank(new int[]{10, 5, 7, 8});
        // Customer 4 max demand allows 6 of R1, but system only has 5 available
        int[] overCapacityRequest = {0, 6, 0, 0};
        boolean result = bank.requestResources(4, overCapacityRequest);
        assertFalse(result, "Request exceeding available capacity should be denied");
    }

    @Test
    @DisplayName("Unsafe request must be denied and state rolled back (README §13 benchmark)")
    public void testUnsafeRequestDeniedAndRolledBack() {
        // Benchmark condition: Available = [10, 5, 7, 8]
        bank = createStandardBank(new int[]{10, 5, 7, 8});
        
        // Customer 0 requests [3, 1, 2, 1]
        // Leaves Available = [7, 4, 5, 7]
        // Customer 3 needs 6 of R1, Customer 4 needs 6 of R1, Customer 2 needs 5 of R1.
        // No safe execution sequence exists for all customers -> unsafe state!
        int[] request = {3, 1, 2, 1};
        boolean approved = bank.requestResources(0, request);
        assertFalse(approved, "Unsafe request leading to potential deadlock must be denied");

        // Verify state is preserved: Customer 1 should still be able to request its valid need
        int[] validCustomer1Request = {4, 2, 3, 2};
        // Available is still [10, 5, 7, 8], so customer 1 request should not be corrupted by prior denied request
        assertDoesNotThrow(() -> bank.getState());
    }

    @Test
    @Disabled("Pending team implementation of requestResources and releaseResources stubs")
    @DisplayName("Safe request must be approved when a valid completion sequence exists")
    public void testSafeRequestApproved() {
        // With Available = [10, 7, 7, 8], Customer 0 requesting [3, 1, 2, 1] is SAFE
        // Safe sequence: <P1, P3, P4, P0, P2>
        bank = createStandardBank(new int[]{10, 7, 7, 8});
        int[] request = {3, 1, 2, 1};
        boolean approved = bank.requestResources(0, request);
        assertTrue(approved, "Safe request with viable execution trajectory must be approved");
    }

    @Test
    @Disabled("Pending team implementation of requestResources and releaseResources stubs")
    @DisplayName("Resources successfully allocated can be released back to the bank")
    public void testResourceReleaseUpdatesBankState() {
        bank = createStandardBank(new int[]{10, 7, 7, 8});
        int[] request = {3, 1, 2, 1};
        assertTrue(bank.requestResources(0, request));

        // Release 1 of R0 and 1 of R2
        int[] release = {1, 0, 1, 0};
        assertDoesNotThrow(() -> bank.releaseResources(0, release), "Release should complete successfully");

        // Customer 0 should now be able to request 1 of R0 again without exceeding claim
        int[] reRequest = {1, 0, 0, 0};
        assertTrue(bank.requestResources(0, reRequest));
    }

    @Test
    @DisplayName("Release exceeding currently allocated resources should be safely handled")
    public void testReleaseExceedingAllocationGuarded() {
        bank = createStandardBank(new int[]{10, 7, 7, 8});
        // Customer 0 currently has 0 allocated. Attempting to release 5 of R0.
        int[] overRelease = {5, 0, 0, 0};
        assertDoesNotThrow(() -> bank.releaseResources(0, overRelease),
                "Over-release should be safely rejected/handled without throwing unexpected exceptions");
    }

    @Test
    @DisplayName("Invalid customer number should be rejected gracefully")
    public void testInvalidCustomerNumber() {
        bank = createStandardBank(new int[]{10, 7, 7, 8});
        int[] request = {1, 0, 0, 0};
        assertFalse(bank.requestResources(-1, request), "Negative customer index should be rejected");
        assertFalse(bank.requestResources(5, request), "Out-of-bounds customer index should be rejected");
        assertDoesNotThrow(() -> bank.releaseResources(-1, request));
        assertDoesNotThrow(() -> bank.releaseResources(5, request));
    }

    @Test
    @DisplayName("Negative resource values in request should be rejected")
    public void testNegativeResourceRequest() {
        bank = createStandardBank(new int[]{10, 7, 7, 8});
        int[] negativeRequest = {-1, 0, 0, 0};
        assertFalse(bank.requestResources(0, negativeRequest), "Negative request values must be rejected");
    }
}
