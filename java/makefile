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

.PHONY: all compile run clean rebuild help

# Default target
all: compile

# Compile Java source files
compile:
	$(JAVAC) -Xlint:all $(SOURCES)

# Run the program
run: compile
	$(JAVA) $(MAIN) $(INPUT_FILE) $(RESOURCES)

# Remove compiled class files
clean:
	rm -f *.class

# Clean and recompile
rebuild: clean compile

# Display available commands
help:
	@echo "Banker's Algorithm - Available Commands"
	@echo "----------------------------------------"
	@echo "make compile  - Compile the project"
	@echo "make run      - Compile and run the project"
	@echo "make clean    - Remove compiled files"
	@echo "make rebuild  - Clean and recompile"
	@echo "make help     - Display this help message"
