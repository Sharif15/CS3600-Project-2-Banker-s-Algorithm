public class BankImpl implements BankInterface {

    private int numberOfCustomers;
    private int numberOfResources;

    // Banker's Algorithm data structures
    private int[] available;
    private int[][] maximum;
    private int[][] allocation;
    private int[][] need;

    /**
     * Creates a new bank with the specified resources.
     */
    public BankImpl(int numberOfCustomers, int[] resources) {

        this.numberOfCustomers = numberOfCustomers;
        this.numberOfResources = resources.length;

        this.available = new int[this.numberOfResources];
        for (int i = 0; i < this.numberOfResources; i++) {
            this.available[i] = resources[i];
        }

        this.maximum = new int[this.numberOfCustomers][this.numberOfResources];
        this.allocation = new int[this.numberOfCustomers][this.numberOfResources];
        this.need = new int[this.numberOfCustomers][this.numberOfResources];
    }

    /**
     * Adds a customer and records the customer's
     * maximum resource demand.
     */
    @Override
    public void addCustomer(int customerNum, int[] maxDemand) {

        for (int j = 0; j < numberOfResources; j++) {
            maximum[customerNum][j] = maxDemand[j];
            need[customerNum][j] = maxDemand[j];
        }
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
    public void getState() {

        System.out.println("Available Resources:");
        for (int j = 0; j < numberOfResources; j++) {
            System.out.print(available[j] + " ");
        }
        System.out.println("\n");

        System.out.println("Maximum Matrix:");
        for (int i = 0; i < numberOfCustomers; i++) {
            for (int j = 0; j < numberOfResources; j++) {
                System.out.print(maximum[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();

        System.out.println("Allocation Matrix:");
        for (int i = 0; i < numberOfCustomers; i++) {
            for (int j = 0; j < numberOfResources; j++) {
                System.out.print(allocation[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();

        System.out.println("Need Matrix:");
        for (int i = 0; i < numberOfCustomers; i++) {
            for (int j = 0; j < numberOfResources; j++) {
                System.out.print(need[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
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
