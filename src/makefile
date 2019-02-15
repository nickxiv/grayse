# Implemented by Nick Martin for CS 403 - Programming Languages
# Sourced from https://www.cs.swarthmore.edu/~newhall/unixhelp/howto_makefiles.html#java

JCC = javac

JFLAGS = -Xlint

JRUN = java Environments

default: 	
	$(JCC) $(JFLAGS) *.java


scanner:
	$(JCC) $(JFLAGS) Scanner.java

test1:
	$(JRUN) test1.gray

test2:
	$(JRUN) test2.gray

test3:
	$(JRUN) test3.gray

test4:
	$(JRUN) test4.gray

test5:
	$(JRUN) test5.gray

test: run

run:
	-$(JRUN)

clean: 
	clear
	$(RM) *.class
	$(RM) *.pp.*
