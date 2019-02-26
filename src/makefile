# Implemented by Nick Martin for CS 403 - Programming Languages
# Sourced from https://www.cs.swarthmore.edu/~newhall/unixhelp/howto_makefiles.html#java

JCC = javac

JFLAGS = -Xlint

JRUN = java Evaluation

default: 	
	@$(JCC) $(JFLAGS) *.java

error1:
	@cat tests/error1.gray

error1x:
	-@$(JRUN) tests/error1.gray

error2:
	@cat tests/error2.gray

error2x:
	-@$(JRUN) tests/error2.gray

error3:
	@cat tests/error3.gray

error3x:
	-@$(JRUN) tests/error3.gray

error4:
	@cat tests/error4.gray

error4x:
	-@$(JRUN) tests/error4.gray

error5:
	@cat tests/error5.gray

error5x:
	-@$(JRUN) tests/error5.gray

arrays:
	@cat tests/arrays.gray

arraysx:
	@$(JRUN) tests/arrays.gray


conditionals:
	@cat tests/conditionals.gray

conditionalsx:
	@$(JRUN) tests/conditionals.gray

recursion:
	@cat tests/recursion.gray

recursionx:
	@$(JRUN) tests/recursion.gray


iteration:
	@cat tests/iteration.gray

iterationx:
	@$(JRUN) tests/iteration.gray

functions:
	@cat tests/functions.gray

functionsx:
	@$(JRUN) tests/functions.gray

lambda:
	@cat tests/lambda.gray

lambdax:
	@$(JRUN) tests/lambda.gray

problem:
	@cat tests/problem.gray

problemx:
	@$(JRUN) tests/problem.gray


clean: 
	@clear
	@$(RM) *.class
