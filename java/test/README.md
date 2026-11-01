# JUnit 5 Testing Suite for Banker's Algorithm

This directory contains automated unit and integration tests for the Banker's Algorithm implementation using the **JUnit 5 (Jupiter)** platform.

## Test Structure
* **`BankImplTest.java`**: Comprehensive test suite verifying:
  1. **System Initialization & State**: Verifies initialization of $Available$, $Maximum$, $Allocation$, and $Need$ matrices without exception.
  2. **Maximum Claim Violation**: Asserts that requests exceeding a customer's declared maximum demand ($\text{Request}_i > \text{Need}_i$) are denied.
  3. **Capacity Violation**: Asserts that requests exceeding the bank's available inventory ($\text{Request}_i > \text{Available}$) are denied.
  4. **Safety Simulation & Deadlock Avoidance**: Replicates the project benchmark (README §13):
     * With Available = `[10 5 7 8]`, Customer 0 requesting `[3 1 2 1]` leads to an unsafe state and is **Denied**.
     * With Available = `[10 7 7 8]`, Customer 0 requesting `[3 1 2 1]` finds the safe sequence $\langle P_1, P_3, P_4, P_0, P_2 \rangle$ and is **Approved**.
  5. **Resource Release**: Verifies that resources released by a customer are correctly returned to the bank's Available pool and decremented from Allocation.
  6. **Over-Release Guard**: Ensures that attempting to release more resources than currently allocated is safely rejected without corrupting bank state.
  7. **Boundary & Negative Input Guards**: Tests out-of-bounds customer indices (e.g., negative or $\ge n$) and negative resource request values.
* **`test_inputs/`**: Sample customer input files for testing various matrix dimensions and edge cases.

## Running Tests via Makefile

From the `java/` directory:

```bash
# Compile and run all JUnit 5 tests
make test

# Compile only
make compile-tests

# Clean up all compiled .class files (including test class files)
make clean
```

The test runner utilizes the standalone JUnit Platform console located in `java/lib/junit-platform-console-standalone-1.10.2.jar`. No additional dependencies, Maven, or Gradle installations are required.
