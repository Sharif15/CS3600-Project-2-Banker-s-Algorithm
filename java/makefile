# Java compiler
JAVAC = javac
JAVA = java

# Project files
SOURCES = BankInterface.java BankImpl.java TestBankers.java
MAIN = TestBankers

# Default input configuration
INPUT_FILE = inPutFile.txt
RESOURCES = 10 7 7 8

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

# Test source files
TEST_SOURCES = $(wildcard test/*Test.java)

# Classpaths

MAIN_CP = .
TEST_CP = .$(CP_SEP)test

.PHONY: all compile run compile-tests test test-unit test-driver clean rebuild help

# Default target
all: compile

# Compile Java source files
compile:
	$(JAVAC) -Xlint:all $(SOURCES)

# Compile test files
compile-tests: compile
	$(JAVAC) -Xlint:all -cp "$(MAIN_CP)$(CP_SEP)$(JUNIT_JAR)" $(TEST_SOURCES)

# Run all JUnit 5 tests
test: compile-tests
	$(JAVA) -jar $(JUNIT_JAR) execute \
		--class-path "$(TEST_CP)" \
		--scan-class-path \
		--details=tree

# Run unit tests only
test-unit: compile-tests
	$(JAVA) -jar $(JUNIT_JAR) execute \
		--class-path "$(TEST_CP)" \
		--select-class BankImplTest \
		--details=tree

# Run driver integration tests only
test-driver: compile-tests
	$(JAVA) -jar $(JUNIT_JAR) execute \
		--class-path "$(TEST_CP)" \
		--select-class TestBankersIntegrationTest \
		--details=tree

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
	@echo "make compile     - Compile application"
	@echo "make run         - Run interactive application"
	@echo "make compile-tests - Compile application and tests"
	@echo "make test        - Run all JUnit tests"
	@echo "make test-unit   - Run BankImpl unit tests"
	@echo "make clean       - Remove compiled files"
	@echo "make rebuild     - Clean and recompile"
	@echo "make help        - Display this help message"
