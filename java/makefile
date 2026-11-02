# Java compiler
JAVAC = javac
JAVA = java

# Project files
SOURCES = BankInterface.java BankImpl.java TestBankers.java
MAIN = TestBankers

# Default input configuration
INPUT_FILE = inPutFile.txt
RESOURCES = 10 5 7 8

# Compiled class files
CLASSES = $(SOURCES:.java=.class)

# Platform-specific configurations
ifeq ($(OS),Windows_NT)
    CP_SEP = ;
    CLEAN_CMD = cmd /c "del /Q /F *.class test\*.class 2>nul"
else
    CP_SEP = :
    CLEAN_CMD = rm -f *.class test/*.class
endif

# JUnit 5 Testing configuration
JUNIT_JAR = lib/junit-platform-console-standalone-1.10.2.jar
TEST_SOURCES = test/BankImplTest.java

.PHONY: all compile run test compile-tests clean rebuild help

# Default target
all: compile

# Compile Java source files
compile:
	$(JAVAC) -Xlint:all $(SOURCES)

# Compile test files
compile-tests: compile
	$(JAVAC) -cp ".$(CP_SEP)$(JUNIT_JAR)" $(TEST_SOURCES)

# Run JUnit 5 tests
test: compile-tests
	$(JAVA) -jar $(JUNIT_JAR) execute --class-path ".$(CP_SEP)test" --scan-class-path

# Run the program
run: compile
	$(JAVA) $(MAIN) $(INPUT_FILE) $(RESOURCES)

# Remove compiled class files
clean:
	-$(CLEAN_CMD)

# Clean and recompile
rebuild: clean compile

# Display available commands
help:
	@echo "Banker's Algorithm - Available Commands"
	@echo "----------------------------------------"
	@echo "make compile  - Compile the project"
	@echo "make run      - Compile and run the project"
	@echo "make test     - Compile and run JUnit 5 tests"
	@echo "make clean    - Remove compiled files"
	@echo "make rebuild  - Clean and recompile"
	@echo "make help     - Display this help message"
