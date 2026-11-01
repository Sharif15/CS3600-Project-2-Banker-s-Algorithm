# Programming Project: Banker's Algorithm

## 1. Project Overview

In this project, you will implement the Banker's Algorithm, a deadlock-avoidance algorithm discussed in the Deadlocks section of the course.

The Banker's Algorithm models a system in which multiple customers compete for a limited number of resource types. Each customer declares the maximum number of resources it may require. During execution, customers may request additional resources or release resources they currently hold.

The banker must determine whether granting a resource request would leave the system in a safe state.

- If granting the request leaves the system in a safe state, the request is approved.
- If granting the request would leave the system in an unsafe state, the request is denied.

You may implement this project using either:

- Java
- C

Starter code is provided for both languages.

---

# 2. System Model

The banker manages:

- `n` customers
- `m` resource types

For this project, there are exactly:

```text
NUMBER_OF_CUSTOMERS = 5
```

The number of resource types is determined when the program is executed.

For example:

```text
10 5 7 8
```

represents four resource types:

| Resource | Initial Instances |
|---|---:|
| R0 | 10 |
| R1 | 5 |
| R2 | 7 |
| R3 | 8 |

Therefore:

```text
m = 4
```

Your program must determine the number of resource types from the command-line arguments. Do **not** hard-code the number of resource types.

---

# 3. Required Data Structures

Your implementation must maintain the following four data structures:

```text
Available
Maximum
Allocation
Need
```

## 3.1 Available

The `available` array contains the number of currently available instances of each resource type.

Conceptually:

```text
available[m]
```

For example:

```text
Available = [10 5 7 8]
```

means:

- 10 instances of R0 are available
- 5 instances of R1 are available
- 7 instances of R2 are available
- 8 instances of R3 are available

---

## 3.2 Maximum

The `maximum` matrix represents the maximum possible demand of every customer.

Conceptually:

```text
maximum[n][m]
```

Each row represents one customer, and each column represents one resource type.

For example:

```text
        R0 R1 R2 R3
C0      6  4  7  3
C1      4  2  3  2
C2      2  5  3  3
C3      6  3  3  2
C4      5  6  7  5
```

---

## 3.3 Allocation

The `allocation` matrix represents the resources currently allocated to each customer.

Conceptually:

```text
allocation[n][m]
```

Initially, no resources have been allocated, so all entries should be `0`.

For example:

```text
        R0 R1 R2 R3
C0      0  0  0  0
C1      0  0  0  0
C2      0  0  0  0
C3      0  0  0  0
C4      0  0  0  0
```

---

## 3.4 Need

The `need` matrix represents the remaining resources that each customer may still request.

Conceptually:

```text
need[n][m]
```

The relationship between `maximum`, `allocation`, and `need` is:

```text
Need[i][j] = Maximum[i][j] - Allocation[i][j]
```

Because the initial allocation is zero:

```text
Need = Maximum
```

when the program begins.

---

# 4. Input File

The program must read each customer's maximum resource demand from an input file.

A sample input file named:

```text
inPutFile.txt
```

is provided.

The file contains:

```text
6,4,7,3
4,2,3,2
2,5,3,3
6,3,3,2
5,6,7,5
```

Each line represents one customer.

For example:

```text
6,4,7,3
```

represents the maximum demand of Customer 0.

The next line:

```text
4,2,3,2
```

represents the maximum demand of Customer 1.

The values on each line are separated by commas.

Your program must:

1. Open the input file.
2. Read one line for each customer.
3. Parse the comma-separated resource values.
4. Store the values in the customer's row of `maximum`.
5. Initialize the corresponding row of `need`.
6. Initialize the corresponding row of `allocation` to zero.

The provided input file contains the five customer maximum-demand rows shown above. 

---

# 5. Program Invocation

Both Java and C implementations must accept:

1. The input filename.
2. The initial quantity of each resource type.

## Java

Compile:

```bash
javac BankInterface.java BankImpl.java TestBankers.java
```

Run:

```bash
java TestBankers inPutFile.txt 10 5 7 8
```

## C

Compile:

```bash
gcc main.c banker.c -o banker
```

Run:

```bash
./banker inPutFile.txt 10 5 7 8
```

For either implementation:

```text
10 5 7 8
```

means:

```text
R0 = 10
R1 = 5
R2 = 7
R3 = 8
```

Therefore, the initial available resources are:

```text
Available = [10 5 7 8]
```

---

# 6. Interactive Commands

After initialization, your program must repeatedly accept commands from the user.

The following commands are required:

| Command | Description |
|---|---|
| `RQ` | Request resources |
| `RL` | Release resources |
| `*` | Display the current state |

The program should continue accepting commands until the program is terminated.

---

# 7. Requesting Resources – `RQ`

The command format for requesting resources is:

```text
RQ <customer> <R0> <R1> ... <Rm-1>
```

For example:

```text
RQ 0 3 1 2 1
```

means:

> Customer 0 requests 3 instances of R0, 1 instance of R1, 2 instances of R2, and 1 instance of R3.

The banker must determine whether this request can safely be granted.

---

# 8. Request Validation

Before granting a request, the banker must perform the following checks.

## Check 1 – Request Must Not Exceed Remaining Need

For every resource type:

```text
Request[i] <= Need[customer][i]
```

Conceptually:

```text
Request <= Need
```

If a customer requests more resources than its declared remaining need, the request must be denied.

---

## Check 2 – Requested Resources Must Be Available

For every resource type:

```text
Request[i] <= Available[i]
```

Conceptually:

```text
Request <= Available
```

If sufficient resources are not currently available, the request must be denied.

---

## Check 3 – Resulting State Must Be Safe

Having enough resources available does not automatically mean the request should be approved.

The banker must determine whether granting the request would leave the system in a safe state.

This requires running the Safety Algorithm.

If a safe sequence exists:

```text
Approved
```

Otherwise:

```text
Denied
```

---

# 9. Safety Algorithm

The Safety Algorithm determines whether all customers could eventually finish.

A state is considered safe if there exists some ordering of the customers such that every customer can obtain its remaining resources, complete its work, and return its allocated resources to the system.

## Step 1 – Create a Temporary State

Create temporary information representing the resources that would be available if the request were granted.

The actual state of the banker must not be permanently modified while testing the request.

---

## Step 2 – Simulate the Request

Temporarily simulate granting the requested resources to the requesting customer.

The simulated state should reflect the corresponding changes to:

```text
Available
Allocation
Need
```

These changes are only temporary while determining whether the request is safe.

---

## Step 3 – Track Customer Completion

Create a way to track whether each customer could finish.

Initially, no customer should be considered finished.

Conceptually:

```text
Finish[0] = false
Finish[1] = false
Finish[2] = false
Finish[3] = false
Finish[4] = false
```

---

## Step 4 – Search for a Customer That Can Finish

Find an unfinished customer whose remaining resource needs can be satisfied using the currently available temporary resources.

Conceptually:

```text
Need[i] <= Work
```

This comparison must be true for every resource type.

---

## Step 5 – Simulate Customer Completion

If a customer can finish, assume that the customer completes its work and returns its allocated resources.

Those resources then become available to other customers.

Mark the customer as able to finish.

---

## Step 6 – Continue Searching

Continue searching for customers that can finish using the newly available resources.

A possible safe sequence might look like:

```text
C1 -> C3 -> C0 -> C2 -> C4
```

The actual sequence depends on the current state of the system.

---

## Step 7 – Determine Safe or Unsafe State

If every customer can eventually finish:

```text
Safe State
```

If one or more customers cannot finish:

```text
Unsafe State
```

A request may only be permanently granted when the resulting state is safe.

---

# 10. Granting a Request

A request may be approved only when:

1. The request does not exceed the customer's remaining need.
2. The requested resources are currently available.
3. Granting the request leaves the system in a safe state.

After an approved request, correctly update:

```text
Available
Allocation
Need
```

The program should report:

```text
Approved
```

---

# 11. Denying a Request

A request must be denied when:

- It exceeds the customer's remaining need.
- There are insufficient currently available resources.
- Granting the request would result in an unsafe state.

The program should report:

```text
Denied
```

A denied request must not change the current state of the banker.

---

# 12. Releasing Resources – `RL`

The command format for releasing resources is:

```text
RL <customer> <R0> <R1> ... <Rm-1>
```

For example:

```text
RL 4 1 2 3 1
```

means:

> Customer 4 releases 1 instance of R0, 2 instances of R1, 3 instances of R2, and 1 instance of R3.

When resources are released, your program must correctly update:

```text
Available
Allocation
Need
```

A customer must not be allowed to release more of a resource than it currently holds.

---

# 13. Displaying the Current State – `*`

When the user enters:

```text
*
```

the program must display the current values of:

```text
Available
Maximum
Allocation
Need
```

For example, the initial state may resemble:

```text
Available:
[10 5 7 8]

Maximum:
Customer 0: [6 4 7 3]
Customer 1: [4 2 3 2]
Customer 2: [2 5 3 3]
Customer 3: [6 3 3 2]
Customer 4: [5 6 7 5]

Allocation:
Customer 0: [0 0 0 0]
Customer 1: [0 0 0 0]
Customer 2: [0 0 0 0]
Customer 3: [0 0 0 0]
Customer 4: [0 0 0 0]

Need:
Customer 0: [6 4 7 3]
Customer 1: [4 2 3 2]
Customer 2: [2 5 3 3]
Customer 3: [6 3 3 2]
Customer 4: [5 6 7 5]
```

Your exact formatting may differ, but all four data structures must be clearly identified.

---

# 14. Required Operations

Regardless of whether you choose Java or C, your program must provide equivalent functionality for the following operations.

## Add Customer

This operation initializes a customer's resource information.

### Java

```java
void addCustomer(int customerNum, int[] maxDemand);
```

### C

```c
void add_customer(
    Bank *bank,
    int customer_num,
    int max_demand[]
);
```

---

## Request Resources

This operation attempts to allocate resources to a customer.

It must:

1. Validate the request.
2. Verify that the request does not exceed the customer's remaining need.
3. Verify that sufficient resources are available.
4. Run the Safety Algorithm.
5. Grant the request only if the resulting state is safe.
6. Update the bank after an approved request.
7. Leave the bank unchanged after a denied request.

### Java

```java
boolean requestResources(
    int customerNum,
    int[] request
);
```

Return:

```text
true  = approved
false = denied
```

### C

```c
int request_resources(
    Bank *bank,
    int customer_num,
    int request[]
);
```

Return:

```text
0  = approved
-1 = denied
```

---

## Release Resources

### Java

```java
void releaseResources(
    int customerNum,
    int[] release
);
```

### C

```c
void release_resources(
    Bank *bank,
    int customer_num,
    int release[]
);
```

---

## Display State

### Java

```java
void getState();
```

### C

```c
void get_state(Bank *bank);
```

---

# 15. Java Starter Project

If you choose Java, use the provided Java starter project.

## Project Organization

```text
BankersProject/
│
├── BankInterface.java
├── BankImpl.java
├── TestBankers.java
└── inPutFile.txt
```

## `BankInterface.java`

This file defines the operations supported by the banker:

```text
addCustomer(...)
getState()
requestResources(...)
releaseResources(...)
```

## `BankImpl.java`

This class contains the implementation of the banker.

It contains the four major data structures:

```text
available
maximum
allocation
need
```

You will complete the appropriate `TODO` sections, including:

- Bank initialization
- Customer initialization
- Safety checking
- Resource requests
- Resource releases
- State display

The Safety Algorithm should be implemented as a private helper method.

For example:

```java
private boolean isSafeState(
    int customerNum,
    int[] request
)
```

## `TestBankers.java`

This file contains:

```java
public static void main(String[] args)
```

It is responsible for:

- Reading command-line arguments
- Reading the input file
- Creating the bank
- Processing interactive commands
- Calling the appropriate methods in `BankImpl`

The actual Banker's Algorithm should **not** be implemented directly in `TestBankers.java`.

---

# 16. C Starter Project

If you choose C, use the provided C starter project.

## Project Organization

```text
BankersProject/
│
├── banker.h
├── banker.c
├── main.c
└── inPutFile.txt
```

C does not provide Java-style classes and interfaces. Instead, this project uses a `struct`, header file, and implementation file to provide a similar modular organization.

The Java and C projects correspond approximately as follows:

| Java | C |
|---|---|
| `BankInterface.java` | `banker.h` |
| `BankImpl.java` | `banker.c` |
| `TestBankers.java` | `main.c` |
| `inPutFile.txt` | `inPutFile.txt` |

## `banker.h`

The header file defines the `Bank` structure and declares the public functions that operate on the bank.

The `Bank` structure contains the information needed to maintain:

```text
number_of_customers
number_of_resources
available
maximum
allocation
need
```

The header file also declares functions such as:

```c
void initialize_bank(...);

void add_customer(...);

int request_resources(...);

void release_resources(...);

void get_state(...);
```

Do not place the main implementation of the Banker's Algorithm in the header file.

## `banker.c`

This file contains the implementation of the banker.

You will complete the appropriate `TODO` sections, including:

- Bank initialization
- Customer initialization
- Safety checking
- Resource requests
- Resource releases
- State display

The Safety Algorithm should be implemented as an internal helper function.

For example:

```c
static int is_safe_state(
    Bank *bank,
    int customer_num,
    int request[]
);
```

## `main.c`

This file contains:

```c
int main(int argc, char *argv[])
```

It is responsible for:

- Reading command-line arguments
- Reading the input file
- Creating and initializing the bank
- Processing commands from the user
- Calling functions implemented in `banker.c`

The actual Banker's Algorithm should not be implemented directly in `main.c`.

---

# 17. Example Program Session

Start the program.

### Java

```bash
java TestBankers inPutFile.txt 10 5 7 8
```

### C

```bash
./banker inPutFile.txt 10 5 7 8
```

Display the initial state:

```text
*
```

Request resources:

```text
RQ 0 3 1 2 1
```

The program runs the Safety Algorithm.

If the resulting state is safe:

```text
Approved
```

Otherwise:

```text
Denied
```

Another customer may request resources:

```text
RQ 2 1 2 1 1
```

A customer may release resources:

```text
RL 0 1 0 1 0
```

Display the updated state:

```text
*
```

The program should continue accepting commands until it is terminated.

---

# 18. Program Requirements

Your implementation must satisfy all of the following requirements.

## Initialization

- Support exactly 5 customers.
- Determine the number of resource types from the command line.
- Read maximum customer demands from the input file.
- Initialize `Available` using the command-line values.
- Initialize `Maximum` using the input file.
- Initialize `Allocation` to zero.
- Initialize `Need` correctly.

## Resource Requests

- Support the `RQ` command.
- Identify the customer making the request.
- Read one request value for every resource type.
- Ensure the request does not exceed the customer's remaining need.
- Ensure sufficient resources are currently available.
- Run the Safety Algorithm.
- Approve only safe requests.
- Deny unsafe requests.
- Correctly update the bank after an approved request.
- Leave the bank unchanged after a denied request.

## Resource Releases

- Support the `RL` command.
- Identify the customer releasing resources.
- Read one release value for every resource type.
- Verify that the customer holds the resources being released.
- Return released resources to `Available`.
- Update `Allocation`.
- Update `Need`.
- Do not allow allocation values to become negative.

## State Display

- Support the `*` command.
- Display `Available`.
- Display `Maximum`.
- Display `Allocation`.
- Display `Need`.

---

# 19. Error Handling

Your program should appropriately handle invalid input.

At minimum, consider:

- Invalid commands
- Invalid customer numbers
- Missing resource values
- Too many or too few resource values
- Negative resource quantities
- Non-numeric resource values
- Missing input files
- Invalid input-file format
- Requests that exceed the customer's remaining need
- Requests that exceed currently available resources
- Attempts to release resources the customer does not hold

Invalid input must not corrupt the state of the banker.

---

# 20. Testing Requirements

You should thoroughly test your program before submission.

## Test 1 – Initial State

Immediately enter:

```text
*
```

Verify:

```text
Allocation = 0
Need = Maximum
Available = initial command-line resources
```

---

## Test 2 – Valid Safe Request

Enter a request that:

- Does not exceed `Need`
- Does not exceed `Available`
- Leaves the system in a safe state

Expected:

```text
Approved
```

Enter:

```text
*
```

and verify that the state was updated correctly.

---

## Test 3 – Request Exceeds Need

Attempt to request more resources than the customer's remaining need.

Expected:

```text
Denied
```

The bank state must remain unchanged.

---

## Test 4 – Insufficient Available Resources

Request more of a resource than is currently available.

Expected:

```text
Denied
```

The bank state must remain unchanged.

---

## Test 5 – Unsafe Request

Create a situation in which enough resources are currently available for a request, but granting the request would leave the system in an unsafe state.

Expected:

```text
Denied
```

This is an important test because it demonstrates the primary purpose of the Banker's Algorithm.

---

## Test 6 – Release Resources

After allocating resources to a customer, release some of them:

```text
RL <customer> <resources...>
```

Verify that:

- `Available` increases appropriately.
- `Allocation` decreases appropriately.
- `Need` increases appropriately.

---

## Test 7 – Invalid Release

Attempt to release more of a resource than the customer currently holds.

The operation should be rejected.

Verify that:

- The state remains unchanged.
- No allocation becomes negative.

---

## Test 8 – Multiple Transactions

Test a sequence of operations such as:

```text
RQ ...
RQ ...
RL ...
RQ ...
*
```

Verify that the bank remains internally consistent after every operation.

---

# 21. Important Relationships

Throughout program execution, the following relationship must remain true:

```text
Need = Maximum - Allocation
```

A successful resource request causes:

```text
Available decreases
Allocation increases
Need decreases
```

A resource release causes:

```text
Available increases
Allocation decreases
Need increases
```

A denied request must cause:

```text
No change to the bank's state
```

A request must never be permanently applied unless the resulting state is safe.

---

# 22. Code Requirements

Regardless of whether you choose Java or C:

- Your program must compile successfully.
- Your program must run without crashing during normal operation.
- Use meaningful variable, function, and method names.
- Properly indent your code.
- Include comments for important sections of your implementation.
- Keep the Banker's Algorithm logic separate from command processing.
- Do not hard-code the number of resource types.
- Do not hard-code the values from `inPutFile.txt`.
- Use the provided starter-code organization.
- Complete all required `TODO` sections.
- Do not unnecessarily modify the provided method/function interfaces.
- Do not include unnecessary compiled files or executables in your repository.

---

# 23. Getting Started and Submitting Your Work

## Getting Started

To get started, clone the provided Git repository and complete the project locally on your own computer.

You may implement the project using either Java or C, using the appropriate starter code included in the repository.

Throughout the development process, you are encouraged to make incremental changes and document your progress by creating commits in your local Git repository.

For example:

```bash
git add .
git commit -m "Implement bank initialization"
```

Continue committing your work as you make meaningful progress on the project. Your Git history should reflect the development of your solution.

---

### Java Implementation

```text
BankInterface.java
BankImpl.java
TestBankers.java
inPutFile.txt
```

Your program should compile using:

```bash
javac BankInterface.java BankImpl.java TestBankers.java
```

and run using:

```bash
java TestBankers inPutFile.txt 10 5 7 8
```

### C Implementation

```text
banker.h
banker.c
main.c
inPutFile.txt
```

Your program should compile using:

```bash
gcc main.c banker.c -o banker
```

and run using:

```bash
./banker inPutFile.txt 10 5 7 8
```

---

# 25. Submitting Your Work

When you are ready to submit your project, make sure that all of your final changes have been committed to your local Git repository.

You can check the status of your repository using:

```bash
git status
```

Make sure there are no important uncommitted changes.

Next, create a Git bundle containing your repository and its complete Git history.

Run:

```bash
git bundle create <team_name>.bundle --all
```

Replace `<team_name>` with your team's name.

For example, if your team name is Awesome, use:

```bash
git bundle create team_awesome.bundle --all
```

This creates:

```text
team_awesome.bundle
```

Use the following naming format:

```text
team_<team_name>.bundle
```

Using the correct filename format is important because it helps identify your team during grading.

You may verify your bundle before submitting it using:

```bash
git bundle verify team_<team_name>.bundle
```

Finally, submit only the `.bundle` file to the project assignment on Canvas before the deadline.
