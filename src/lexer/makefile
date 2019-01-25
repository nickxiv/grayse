# Implemented by Nick Martin for CS 403 - Programming Languages
# Sourced from https://www.cs.swarthmore.edu/~newhall/unixhelp/howto_makefiles.html#java

JCC = javac

JFLAGS = -Xlint

JRUN = scanner

default: 	
	$(JCC) $(JFLAGS) Scanner.java


scanner:
	$(JCC) $(JFLAGS) Scanner.java

test1:
	$(JCC) $(JFLAGS) Scanner.java
	$(JRUN) test1.gray

test2:
	$(JCC) $(JFLAGS) Scanner.java
	$(JRUN) test2.gray

test3:
	$(JCC) $(JFLAGS) Scanner.java
	$(JRUN) test3.gray

test4:
	$(JCC) $(JFLAGS) Scanner.java
	$(JRUN) test4.gray

test5:
	$(JCC) $(JFLAGS) Scanner.java
	$(JRUN) test5.gray

run:
	$(JCC) $(JFLAGS) Scanner.java
	$(JRUN) test1.gray
	$(JRUN) test2.gray
	$(JRUN) test3.gray
	$(JRUN) test4.gray
	$(JRUN) test5.gray

clean: 
	$(RM) *.class
