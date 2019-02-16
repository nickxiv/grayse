# Implemented by Nick Martin for CS 403 - Programming Languages
# Sourced from https://www.cs.swarthmore.edu/~newhall/unixhelp/howto_makefiles.html#java

JCC = javac

JFLAGS = -Xlint

JRUN = java PP

default: 	
	$(JCC) $(JFLAGS) *.java


scanner:
	$(JCC) $(JFLAGS) Scanner.java

# test1:
# 	$(JRUN) test1.gray

# test2:
# 	$(JRUN) test2.gray

# test3:
# 	$(JRUN) test3.gray

# test4:
# 	$(JRUN) test4.gray

# test5:
# 	$(JRUN) test5.gray

# test: run

run: test1 test2 test3

clean: 
	clear
	$(RM) *.class
	$(RM) *.pp.*


test1:
	@echo Original file:
	@cat test1.gray
	@echo Pretty Printed version of the original:
	@$(JRUN) test1.gray > test1.pp.1
	@cat test1.pp.1
	@echo Pretty Printed version of the pretty printed version:
	@$(JRUN) test1.pp.1 > test1.pp.2
	@cat test1.pp.2
	diff -s -q test1.pp.1 test1.pp.2

test2:
	@echo Original file:
	@cat test2.gray
	@echo Pretty Printed version of the original:
	@$(JRUN) test2.gray > test2.pp.1
	@cat test2.pp.1
	@echo Pretty Printed version of the pretty printed version:
	@$(JRUN) test2.pp.1 > test2.pp.2
	@cat test2.pp.2
	diff -s -q test2.pp.1 test2.pp.2

test3:
	@echo Original file:
	@cat test3.gray
	@echo Pretty Printed version of the original:
	@$(JRUN) test3.gray > test3.pp.1
	@cat test3.pp.1
	@echo Pretty Printed version of the pretty printed version:
	@$(JRUN) test3.pp.1 > test3.pp.2
	@cat test3.pp.2
	diff -s -q test3.pp.1 test3.pp.2
