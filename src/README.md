# Lexer is a module within the Grayse programming language, written by Nick Martin for CS 403 Programming Languages

# The first class within the module is "Scanner"
# Usage is "scanner FFFF" where FFFF is the name of a file written in Grayse

# Step by step scanner usage:
 1. Compile Scanner.java using provided makefile or typing javac Scanner.java
 2. Type scanner FFFF where FFFF is your Grayse file name
 3. Done!

# The second class within the module is "Recognizer"
# Usage is "recognizer FFFF" where FFFF is the name of a file written in Grayse

# Step by step recognizer usage:
 1. Compile Recognizer.java using provided makefile or typing javac Recognizer.java
 2. Type recognizer FFFF where FFFF is your Grayse file name
 3. Done!

# The third class within the module is "Environments"
# Usage is "environments"
# This class tests functionality of building environments using a hard-coded testing

# Step by step environments usage:
 1. Compile Environments.java using provided makefile or typing javac Environments.java
 2. Type environments
 3. Done!


# With the provided makefile, typing "make" will compile all java classes.
# "make run" will run the environments test
# "make clean" will delete all .class files

# Expected output of included test files when run with Recognizer:

test1.gray:
legal

test2.gray:
illegal line: 14, expected: VARIABLE

test3.gray:
legal

test4.gray:
illegal line: 10, expected: SEMICOLON

test5.gray (exits with error code of 1):
illegal line: 6, expected: SEMICOLON


