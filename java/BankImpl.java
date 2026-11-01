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
        for (int i = 0; i < this.numberOfResources; i++){
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
                allocation[customerNum][j] = 0;
        }
    }

    /**
     * Displays the current state of the bank:
     * Available, Maximum, Allocation, and Need.
     */
    @Override
        public void getState() {
            System.out.println("Available:");
            System.out.print("[");
            for (int i = 0; i < numberOfResources; i++) {
                System.out.print(available[i] + (i <
                numberOfResources - 1 ? " " : ""));
            }
            System.out.println("]\n");

            
            printMatrix("Maximum", maximum);
            printMatrix("Allocation", allocation);
            printMatrix("Need", need);
        }

        private void printMatrix(String title, int[][] matrix) {
            System.out.println(title + ":");
            for (int i = 0; i < numberOfCustomers; i++) {
                System.out.print("Customer " + i + ": [");
                for (int j = 0; j < numberOfResources; j++) {
                    System.out.print(matrix[i][j] + (j <
                    numberOfResources - 1 ? " " : ""));
                }
                System.out.println("]");
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

        // (Work, TempAlloc, tempNeed)
        int[] work = new int[numberOfResources];
        for(int j = 0; j < numberOfResources; j++) {
            work[j] = available[j] - request[j];
        }
    
        int[][] tempAlloc = new
            int[numberOfCustomers][numberOfResources];
            int[][] tempNeed = new
            int[numberOfCustomers][numberOfResources];
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
                        for (int j = 0; j < numberOfResources;
                        j++) {
                            if (tempNeed[i][j] > work[j]) {
                                canFinish = false;
                                break;
                            }
                        }

                        if (canFinish) {
                            for (int j = 0; j < numberOfResources;
                            j++) {
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
    public synchronized boolean requestResources(int customerNum, int[] request) {

        if (customerNum < 0 || customerNum >= numberOfCustomers) {
            return false;
        }

        for(int j = 0; j < numberOfResources; j++) {
            if (request[j] < 0 || request[j] > need[customerNum][j]) {
                return false;
            }
        }

        for (int j = 0; j < numberOfResources; j++) {
            if (request[j] > available[j]) {
                return false;
            }
        }

        if(!isSafeState(customerNum, request)) {
            return false;
        }
        for (int j = 0; j < numberOfResources; j++) {
                available[j] -= request[j];
                allocation[customerNum][j] += request[j];
                need[customerNum][j] -= request[j];
            }

        return true;
    }

    /**
     * Releases resources held by a customer.
     */
    @Override
    public synchronized void releaseResources(int customerNum, int[] release) {

        if (customerNum < 0 || customerNum >= numberOfCustomers){
            return;
        }

        for (int j = 0; j < numberOfResources; j++) {
                if (release[j] > allocation[customerNum][j]  || release [j] < 0) {
                    return;
                }
            }
        for(int j = 0; j < numberOfResources; j++) {
            available[j] += release[j];
            allocation[customerNum][j] -= release[j];
            need[customerNum][j] += release[j];
        }
    }
}
