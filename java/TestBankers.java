
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestBankers {

    public static final int NUMBER_OF_CUSTOMERS = 5;

    public static void main(String[] args) throws IOException {

        /*
         * Usage:
         * java TestBankers <input-file> <R0> <R1> ... <Rm-1>
         */

        if (args.length < 2) {
            printUsage();
            return;
        }

        try {
            // 1. Read the input filename.
            String inputFile = args[0];

            // 2. Determine the number of resource types.
            int numberOfResources = args.length - 1;

            // 3. Read initial resource quantities.
            int[] initialResources = new int[numberOfResources];

            for (int i = 0; i < numberOfResources; i++) {
                initialResources[i] = Integer.parseInt(args[i + 1]);

                if (initialResources[i] < 0) {
                    throw new IllegalArgumentException(
                        "Resource quantities cannot be negative."
                    );
                }
            }

            // 4. Create the bank.
            BankImpl bank = new BankImpl(
                NUMBER_OF_CUSTOMERS,
                initialResources
            );

            // 5. Read maximum demand from the input file.
            try (BufferedReader reader =
                    new BufferedReader(new FileReader(inputFile))) {

                String line;
                int customerNum = 0;

                while ((line = reader.readLine()) != null) {

                    // Ignore empty lines.
                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    if (customerNum >= NUMBER_OF_CUSTOMERS) {
                        throw new IllegalArgumentException(
                            "Input file contains more than "
                            + NUMBER_OF_CUSTOMERS + " customers."
                        );
                    }

                    // 6. Parse the customer's maximum demand.
                    String[] values = line.split(",");

                    if (values.length != numberOfResources) {
                        throw new IllegalArgumentException(
                            "Customer " + customerNum
                            + " has an incorrect number of resource values."
                        );
                    }

                    int[] maxDemand = new int[numberOfResources];

                    for (int j = 0; j < numberOfResources; j++) {
                        maxDemand[j] = Integer.parseInt(values[j].trim());

                        if (maxDemand[j] < 0) {
                            throw new IllegalArgumentException(
                                "Maximum demand cannot be negative."
                            );
                        }
                    }

                    // 7. Register the customer.
                    bank.addCustomer(customerNum, maxDemand);

                    customerNum++;
                }

                if (customerNum != NUMBER_OF_CUSTOMERS) {
                    throw new IllegalArgumentException(
                        "Expected " + NUMBER_OF_CUSTOMERS
                        + " customers, but found " + customerNum + "."
                    );
                }
            }

            System.out.println("Bank initialized successfully.");
            System.out.println("Number of customers: "
                + NUMBER_OF_CUSTOMERS);
            System.out.println("Number of resource types: "
                + numberOfResources);

            // 8. Process interactive commands.
            processCommands(bank, numberOfResources);

        } catch (NumberFormatException e) {
            System.err.println("ERROR: Invalid numeric input.");
        } catch (IllegalArgumentException e) {
            System.err.println("ERROR: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("ERROR: Could not read input file: "
                + e.getMessage());
        }
    }

    /**
     * Reads and processes commands from standard input.
     */
    private static void processCommands(
            BankImpl bank,
            int numberOfResources) throws IOException {

        BufferedReader input = new BufferedReader(
            new InputStreamReader(System.in)
        );

        System.out.println();
        System.out.println("Banker's Algorithm");
        System.out.println("Commands:");
        System.out.println("RQ <customer> <resources...>");
        System.out.println("RL <customer> <resources...>");
        System.out.println("* (display system state)");
        System.out.println("Enter Q or Ctrl+C (Linux/Mac) or Ctrl+Z (Windows) to exit.");
        System.out.println();

        String line;

        while (true) {
            System.out.print("Enter command: ");

            // EOF exits the command loop.
            line = input.readLine();

            if (line == null) {
                System.out.println("\nExiting Banker's Algorithm.");
                break;
            }

            line = line.trim();

            if (line.isEmpty()) {
                continue;
            }

            String[] tokens = line.split("\\s+");

            // Display current state.
            if (tokens.length == 1 && tokens[0].equals("*")) {
                bank.getState();
                continue;
            }

            // Handle exit 
            if (tokens.length == 1 && (tokens[0].equals("Q")||tokens[0].equals("q")||tokens[0].equals("exit"))) {
                break;
            }

            // All other commands require a command, customer, and vector.
            if (tokens.length != numberOfResources + 2) {
                System.out.println(
                    "ERROR: Incorrect number of command arguments."
                );
                continue;
            }

            String command = tokens[0].toUpperCase();

            if (!command.equals("RQ") && !command.equals("RL")) {
                System.out.println("ERROR: Unknown command: " + command);
                continue;
            }

            try {
                // Parse customer number.
                int customerNum = Integer.parseInt(tokens[1]);

                // Parse resource vector.
                int[] resources = new int[numberOfResources];

                for (int i = 0; i < numberOfResources; i++) {
                    resources[i] = Integer.parseInt(tokens[i + 2]);
                }

                // Execute the requested operation.
                if (command.equals("RQ")) {
                    bank.requestResources(customerNum, resources);
                } else {
                    bank.releaseResources(customerNum, resources);
                }

            } catch (NumberFormatException e) {
                System.out.println(
                    "ERROR: Customer number and resource values "
                    + "must be integers."
                );
            }
        }
    }

    private static void printUsage() {
        System.err.println(
            "Usage: java TestBankers "
            + "<input-file> <R0> <R1> ... <Rm-1>"
        );

        System.err.println(
            "Example: java TestBankers inPutFile.txt 10 7 7 8"
        );
    }
}
