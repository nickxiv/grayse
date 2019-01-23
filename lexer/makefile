# Implemented by Nick Martin for CS 403 - Programming Languages
# Sourced from https://www.cs.swarthmore.edu/~newhall/unixhelp/howto_makefiles.html#java

JCC = javac

JFLAGS = -Xlint

JRUN = scanner

default: scanner

scanner: Scanner.java
	$(JCC) $(JFLAGS) Scanner.java

test1:
	$(JCC) $(JFLAGS) Scanner.java
	$(JRUN) test1.txt

test2:
	$(JCC) $(JFLAGS) Scanner.java
	$(JRUN) test2.txt

test3:
	$(JCC) $(JFLAGS) Scanner.java
	$(JRUN) test3.txt

test4:
	$(JCC) $(JFLAGS) Scanner.java
	$(JRUN) test4.txt

test5:
	$(JCC) $(JFLAGS) Scanner.java
	$(JRUN) test5.txt

run:
	$(JCC) $(JFLAGS) Scanner.java
	$(JRUN) test1.txt
	$(JRUN) test2.txt
	$(JRUN) test3.txt
	$(JRUN) test4.txt
	$(JRUN) test5.txt

clean: 
	$(RM) *.class
