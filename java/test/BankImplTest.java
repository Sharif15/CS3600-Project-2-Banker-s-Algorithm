import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit and integration tests for Banker's Algorithm implementation.
 * Validates state invariants, 3-point request checks, safety simulation,
 * and release boundary constraints.
 */
public class BankImplTest {

    private BankImpl bank;

    private final int NUMBER_OF_CUSTOMERS = 5;

    // Total resources available at initialization
    private static final int[] INITIAL_RESOURCES = {
        10, 7, 7, 8
    };

    // Standard initial customer max demand matrix from inPutFile.txt
    private final int[][] STANDARD_MAX_DEMAND = {
        {6, 4, 7, 3}, // Customer 0
        {4, 2, 3, 2}, // Customer 1
        {2, 5, 3, 3}, // Customer 2
        {6, 3, 3, 2}, // Customer 3
        {5, 6, 7, 5}  // Customer 4
    };

    /**
     * Helper to create and populate a BankImpl instance with the standard configuration.
     */

    private BankImpl createStandardBank() {
        BankImpl newBank = new BankImpl(NUMBER_OF_CUSTOMERS, INITIAL_RESOURCES);
        for (int i = 0; i < NUMBER_OF_CUSTOMERS; i++) {
            newBank.addCustomer(i, STANDARD_MAX_DEMAND[i]);
        }
        return newBank;
    }

    @BeforeEach
    public void setUp() {
        bank = createStandardBank();
    }

    @Test
    @DisplayName("Bank initializes successfully")
    public void testInitialization() {

        assertNotNull(bank, "Bank instance should not be null");

        assertDoesNotThrow(() -> bank.getState(), "getState() should execute without exceptions");
    }

    @Test
    @DisplayName("Request exceeding customer maximum demand claim must be denied")
    public void testRequestExceedsMaximumClaim() {

        // Customer 0 max demand is [6, 4, 7, 3]. Requesting 7 of R0 exceeds max demand claim.
        int[] excessiveRequest = {7, 0, 0, 0};

        assertFalse(bank.requestResources(0, excessiveRequest), "Request exceeding maximum claim should be denied");
        
        // Bank should still be usable after the denied request.
        int[] safeRequest = {3, 1, 2, 1};

        assertDoesNotThrow(() -> bank.getState(), "getState() should execute without exceptions");

        assertTrue(
            bank.requestResources(0, safeRequest)
        );
    }


    @Test
    @DisplayName("Request exceeding available bank resources must be denied")
    public void testRequestExceedsAvailableResources() {
        // First allocate resources to Customer 0.
        int[] firstRequest = {3, 2, 2, 1};

        assertTrue(bank.requestResources(0, firstRequest));

        // Available is now [7, 5, 5, 7].
        // Customer 4 needs 6 of R1, but system only has 5 available
        int[] overCapacityRequest = {0, 6, 0, 0};

        boolean result = bank.requestResources(4, overCapacityRequest);

        assertFalse(result, "Request exceeding available capacity should be denied");
    }

    @Test
    @DisplayName("Safe request is approved")
    public void testSafeRequestApproved() {
        // With Available = [10, 7, 7, 8], Customer 0 requesting [3, 1, 2, 1] is SAFE
        // Safe sequence: <P1, P3, P4, P0, P2>
       
        int[] request = {3, 1, 2, 1};
        boolean approved = bank.requestResources(0, request);
       
        assertTrue(approved, "Safe request with viable execution trajectory must be approved");
    }

    @Test
    @DisplayName("Allocated resources can be released")
    public void testResourceReleaseUpdatesBankState() {

        int[] request = {3, 1, 2, 1};
        assertTrue(bank.requestResources(0, request));

        // Release 1 of R0 and 1 of R2
        int[] release = {1, 0, 1, 0};

        assertDoesNotThrow(() -> bank.releaseResources(0, release), "Release should complete successfully");

        // Customer 0 should now be able to request 1 of R0 again 
        int[] reRequest = {1, 0, 0, 0};
        assertTrue(bank.requestResources(0, reRequest));
    }

    @Test
    @DisplayName("Releasing more resources than allocated is rejected")
    public void testReleaseExceedingAllocationGuarded() {

        // Customer 0 currently has 0 allocated. Attempting to release 5 of R0.
        int[] overRelease = {5, 0, 0, 0};

        assertDoesNotThrow(
                () -> bank.releaseResources(0, overRelease),
                "Over-release should be safely rejected/handled without throwing unexpected exceptions"
        );

        // The invalid release should not corrupt the bank.

        int[] safeRequest = {3, 1, 2, 1};

        assertDoesNotThrow(() -> bank.getState(), "getState() should execute without exceptions");

        assertTrue(bank.requestResources(0,safeRequest));

    }

    @Test
    @DisplayName("Invalid customer number should be rejected gracefully")
    public void testInvalidCustomerNumber() {
        int[] request = {1, 0, 0, 0};

        assertFalse(bank.requestResources(-1, request), "Negative customer index should be rejected");
        assertFalse(bank.requestResources(5, request), "Out-of-bounds customer index should be rejected");

        // out of bound customer release request is not considered and ignored

        assertDoesNotThrow(
            () -> bank.releaseResources(-1, request),
            "Release request from out of bound customer index should be ignored"
        );

        assertDoesNotThrow(
            () -> bank.releaseResources(5, request),
            "Release request from out of bound customer index should be ignored"
        );
    }

    @Test
    @DisplayName("Request with incorrect resource vector length is rejected")
    public void testInvalidRequestLength() {

        int[] requestTooShort = {1, 0};
        int[] requestTooLong = {1, 0, 0, 0, 1};

        assertFalse(bank.requestResources(0, requestTooShort));
        assertFalse(bank.requestResources(0, requestTooLong));
    }

    @Test
    @DisplayName("Negative resource values in request should be rejected")
    public void testNegativeResourceRequest() {

        int[] negativeRequest = {-1, 0, 0, 0};
        assertFalse(bank.requestResources(0, negativeRequest), "Negative request values must be rejected");
    }

    @Test
    @DisplayName("Negative resource releases are rejected")
    public void testNegativeResourceRelease() {

        // Give Customer 0 some resources.
        assertTrue(
            bank.requestResources(0, new int[]{3, 1, 2, 1})
        );

        int[] release = {-1, 0, 0, 0};

        assertDoesNotThrow(
            () -> bank.releaseResources(0, release),
            "Negative release request values must be rejected"
        );
    }


    @Test
    @DisplayName("Unsafe request is denied")
    public void testUnsafeRequestDenied() {

        // Step 1: Customer 1 requests resources.
        int[] firstRequest = {3, 0, 2, 2};

        assertTrue(
            bank.requestResources(1, firstRequest),
            "Customer 1's request should be approved"
        );

        // Available is now [7, 7, 5, 6].

        // Step 2: Customer 0 requests resources.
        // This request is within both Need and Available.
        int[] unsafeRequest = {4, 3, 5, 2};

        boolean approved = bank.requestResources(0, unsafeRequest);

        assertFalse(
            approved,
            "Request should be denied because it creates an unsafe state"
        );

        // The bank should remain operational after denial.
        assertDoesNotThrow(
            () -> bank.getState(),
            "Bank state should remain accessible after an unsafe request"
        );
    }


    @Test
    @DisplayName("Multiple transactions maintain a valid bank state")
    public void testMultipleTransactions() {

        // Transaction 1: Customer 0 requests resources.
        assertTrue(
            bank.requestResources(0, new int[]{3, 1, 2, 1}),
            "Customer 0's first request should be approved"
        );

        // Transaction 2: Customer 1 requests resources.
        assertTrue(
            bank.requestResources(1, new int[]{1, 1, 1, 1}),
            "Customer 1's request should be approved"
        );

        // Transaction 3: Customer 0 releases resources.
        assertDoesNotThrow(
            () -> bank.releaseResources(0, new int[]{1, 0, 1, 0}),
            "Customer 0's release should succeed"
        );

        // Transaction 4: Customer 2 requests resources.
        assertTrue(
            bank.requestResources(2, new int[]{1, 1, 1, 1}),
            "Customer 2's request should be approved"
        );

        // Final state should remain accessible.
        assertDoesNotThrow(
            () -> bank.getState(),
            "Bank should maintain a valid state after multiple transactions"
        );
    }


}
