import java.util.Arrays;

public class BankImpl implements BankInterface {

    private final int numberOfCustomers;
    private final int numberOfResources;

    // Banker's Algorithm data structures
    private int[] available;
    private int[][] maximum;
    private int[][] allocation;
    private int[][] need;

    /**
     * Creates a new bank with the specified resources.
     */
    public BankImpl(int numberOfCustomers, int[] resources) {

        // Validating constructor input
        if(numberOfCustomers != 5){
            throw new IllegalArgumentException(
                "Customer number must be 5"
            );
        }

        if (resources == null || resources.length == 0) {
            throw new IllegalArgumentException(
                "Resource array cannot be null or empty."
            );
        }

        for(int i = 0; i < resources.length; i++){
            if(resources[i] < 0){
                throw new IllegalArgumentException(
                    "Total resources cannot be negative"
                );
            }
        }


        this.numberOfCustomers = numberOfCustomers; // could hard code to 5 also for this specific project
        this.numberOfResources = resources.length;

        this.available = new int[numberOfResources];

        for (int i = 0; i < numberOfResources; i++) {
            available[i] = resources[i];
        }

        this.maximum = new int[this.numberOfCustomers][numberOfResources];
        this.allocation = new int[this.numberOfCustomers][numberOfResources];
        this.need = new int[this.numberOfCustomers][numberOfResources];
    }

    /**
     * Adds a customer and records the customer's
     * maximum resource demand.
     */
    
    @Override
    public void addCustomer(int customerNum, int[] maxDemand) {

      // Validate customer number
        if (customerNum < 0 || customerNum >= numberOfCustomers) {
            throw new IllegalArgumentException(
                "Invalid customer number: " + customerNum
            );
        }

        // Validate demand array
        if (maxDemand == null || maxDemand.length != numberOfResources) {
            throw new IllegalArgumentException(
                "Maximum demand must contain exactly "
                + numberOfResources + " resource values."
            );
        }

        // Validate each resource demand
        for (int i = 0; i < numberOfResources; i++) {

            if (maxDemand[i] < 0) {
                throw new IllegalArgumentException(
                    "Maximum demand cannot be negative."
                );
            }

            if (maxDemand[i] > available[i]) {
                throw new IllegalArgumentException(
                    "Customer demand exceeds total resources "
                    + "for resource type " + i
                );
            }
        }

        // Store maximum demand
        maximum[customerNum] = Arrays.copyOf(
            maxDemand,
            numberOfResources
        );

        // Initially, allocation is zero
        Arrays.fill(allocation[customerNum], 0);

        // Initially, Need = Maximum - Allocation
        for (int i = 0; i < numberOfResources; i++) {
            need[customerNum][i] =
                maximum[customerNum][i] - allocation[customerNum][i];
        }
    }

    /**
     * Helper method to format and display a 1D resource vector.
     *
     * @param title the label of the vector (e.g. "Available")
     * @param vector the 1D array of resource counts
     */
    private void printVector(String title, int[] vector) {
        System.out.println(title + ":");
        System.out.print("[");
        for (int j = 0; j < numberOfResources; j++) {
            System.out.print(vector[j]);
            if (j < numberOfResources - 1) {
                System.out.print(" ");
            }
        }
        System.out.println("]");
        System.out.println();
    }

    /**
     * Helper method to format and display a 2D resource matrix.
     * Reusable across Maximum, Allocation, and Need to avoid duplicate code.
     *
     * @param title the label of the matrix (e.g. "Maximum", "Allocation", "Need")
     * @param matrix the 2D customer-by-resource matrix to display
     */
    private void printMatrix(String title, int[][] matrix) {
        System.out.println(title + ":");
        for (int i = 0; i < numberOfCustomers; i++) {
            System.out.print("Customer " + i + ": [");
            for (int j = 0; j < numberOfResources; j++) {
                System.out.print(matrix[i][j]);
                if (j < numberOfResources - 1) {
                    System.out.print(" ");
                }
            }
            System.out.println("]");
        }
        System.out.println();
    }

    /**
     * Displays the current state of the bank:
     *
     * Available
     * Maximum
     * Allocation
     * Need
     */
    @Override
    public synchronized void getState() {
        printVector("Available", available);
        printMatrix("Maximum", maximum);
        printMatrix("Allocation", allocation);
        printMatrix("Need", need);
    }

    /**
     * Determines whether the system would remain
     * in a safe state if the request were granted.
     *
     * (The Banker's Safety Algorithm)
     */
    private boolean isSafeState(int customerNum, int[] request) {

        int[] work = new int[numberOfResources];
        for (int j = 0; j < numberOfResources; j++) {
            work[j] = available[j] - request[j];
        }

        int[][] tempAlloc = new int[numberOfCustomers][numberOfResources];
        int[][] tempNeed = new int[numberOfCustomers][numberOfResources];
        for (int i = 0; i < numberOfCustomers; i++) {
            for (int j = 0; j < numberOfResources; j++) {
                tempAlloc[i][j] = allocation[i][j];
                tempNeed[i][j] = need[i][j];
            }
        }

        for (int j = 0; j < numberOfResources; j++) {
            tempAlloc[customerNum][j] += request[j];
            tempNeed[customerNum][j] -= request[j];
        }

        boolean[] finish = new boolean[numberOfCustomers];
        int finishedCount = 0;

        boolean found;
        do {
            found = false;
            for (int i = 0; i < numberOfCustomers; i++) {
                if (!finish[i]) {
                    boolean canFinish = true;
                    for (int j = 0; j < numberOfResources; j++) {
                        if (tempNeed[i][j] > work[j]) {
                            canFinish = false;
                            break;
                        }
                    }

                    if (canFinish) {
                        for (int j = 0; j < numberOfResources; j++) {
                            work[j] += tempAlloc[i][j];
                        }
                        finish[i] = true;
                        finishedCount++;
                        found = true;
                    }
                }
            }
        } while (found);

        return finishedCount == numberOfCustomers;
    }

    /**
     * Attempts to allocate resources to a customer.
     *
     * @return true if approved
     *         false if denied
     */
    @Override
    public synchronized boolean requestResources(
            int customerNum, int[] request) {

        // 1 & 2. Check whether the request exceeds Need
        for (int j = 0; j < numberOfResources; j++) {
            if (request[j] > need[customerNum][j]) {
                System.out.println("DENIED: Request exceeds maximum declared need.");
                return false;
            }
        }

        // 3. Check whether the resources are available
        for (int j = 0; j < numberOfResources; j++) {
            if (request[j] > available[j]) {
                System.out.println("DENIED: Insufficient instances available right now.");
                return false;
            }
        }

        // 4. Check whether the resulting state is safe using Christopher's algorithm
        if (!isSafeState(customerNum, request)) {
            System.out.println("DENIED: System allocation leads to an unsafe state.");
            return false;
        }

        // 5. Update the bank matrices since the request is approved safely
        for (int j = 0; j < numberOfResources; j++) {
            available[j] -= request[j];
            allocation[customerNum][j] += request[j];
            need[customerNum][j] -= request[j];
        }

        System.out.println("APPROVED: Request allocated successfully.");
        return true;
    }

    /**
     * Releases resources held by a customer.
     */
    @Override
    public synchronized void releaseResources(
            int customerNum, int[] release) {

        for (int j = 0; j < numberOfResources; j++) {
            if (release[j] > allocation[customerNum][j]) {
                System.out.println("ERROR: Cannot release more resources than allocated.");
                return;
            }
        }

        for (int j = 0; j < numberOfResources; j++) {
            available[j] += release[j];
            allocation[customerNum][j] -= release[j];
            need[customerNum][j] += release[j];
        }
        System.out.println("SUCCESS: Resources released.");
    }
}
