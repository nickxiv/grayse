# Implemented by Nick Martin for CS 403 - Programming Languages
# Sourced from https://www.cs.swarthmore.edu/~newhall/unixhelp/howto_makefiles.html#java

JCC = javac

JFLAGS = -Xlint

JRUN = java Evaluation

default: 	
	@$(JCC) $(JFLAGS) *.java


test1:
	$(JRUN) tests/test1.gray

test2:
	$(JRUN) tests/test2.gray

test3:
	$(JRUN) tests/test3.gray

test4:
	@$(JRUN) tests/test4.gray

test5:
	@$(JRUN) tests/test5.gray

error1:
	@cat /tests/error1.gray

error1x:
	@$(JRUN) tests/error1.gray

error2:
	@cat /tests/error2.gray

error2x:
	@$(JRUN) tests/error2.gray

error3:
	@cat /tests/error3.gray

error3x:
	@$(JRUN) tests/error3.gray

error4:
	@cat /tests/error4.gray

error4x:
	@$(JRUN) tests/error4.gray

error5:
	@cat /tests/error5.gray

error5x:
	@$(JRUN) tests/error5.gray

conditionals:
	@cat tests/conditionals.gray

conditionalsx:
	@$(JRUN) tests/conditionals.gray

test: run

run: test1 test2 test3

clean: 
	@clear
	@$(RM) *.class
