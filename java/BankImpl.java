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

        // TODO
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

        // TODO
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

        // TODO:
        // 1. Validate the request.
        // 2. Check whether the request exceeds Need.
        // 3. Check whether the resources are available.
        // 4. Check whether the resulting state is safe.
        // 5. Update the bank if the request is approved.

        return false;
    }

    /**
     * Releases resources held by a customer.
     */
    @Override
    public synchronized void releaseResources(
            int customerNum, int[] release) {

        // TODO:
        // Validate and release the resources.
    }
}
