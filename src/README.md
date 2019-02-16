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

# The fourth class within the module is "Pretty Printer"
# Usage is "pp FFFF" where FFFF is the name of a file written in Grayse
# This class builds a parse tree of lexeme and prints them in order (not pretty)

# Step by step pretty printer usage:
 1. Compile PP.java using provided makefile or typing javac PP.java
 2. Type recognizer FFFF where FFFF is your Grayse file name
 3. Done!



# With the provided makefile, typing "make" will compile all java classes.
# "make run" will run the pretty printer tests
# "make clean" will delete all .class .pp.* files

# Expected output of included test files when run with makefile commands:

test1:
Original file:
//Written by Nick Martin for CS403 - Programming Languages

//The purpose of test4 is to be the first parsing test

let a = 1;
let b = 1 + 1 - a;
let c;
let d, e;
let f, g = 6 - 1;
let h = a + b;

let cool = TRUE;

let name = "Nick";

let classes = [
    "CS300",
    "CS301",
    "CS403",
    "CS491"
];

if (b == 3) {
    b = 4;
};

while (b < 10) {
    b = b + 1;
};

func doNothing() {
    return;
};

func square(num) {
    doNothing();
    RETURN (num * num);
};

func add(a, b) {
    RETURN (a + b);
};

i = square(2);
j = add(square(2), square(3));


func getNestedFunc(num) {
    func nestedMaker(x) {
        return (x % num);
    };
    return nestedMaker;
};

class Person {
    age;
    firstName;
    lastName;
    smart;
    someFunc;
    yearInSchool;
    classes;
    classKeys;
};

let person = {
    age: 20;
    firstName: name;
    lastName: "Martin";
    smart: TRUE;
    someFunc: doNothing();
    yearInSchool: (5 - 1);
    classes: classes;
    classKeys: [
        403,
        491,
        300,
        301
    ];
};

let emptyObj = {};

person.age = 21;

c = square(b);


if (cool & TRUE) {
    f = 8;
} else if (cool) {
    f = 9;
} else {
    f = 10;
};

if (9 == 10) {
    print("Math broke!");
};

return 0;


Pretty Printed version of the original:
let a = 1;
let b = 1 + 1 - a;
let c;
let d, e;
let f, g = 6 - 1;
let h = a + b;
let cool = true;
let name = "Nick";
let classes = 
[
"CS300", 
"CS301", 
"CS403", 
"CS491"
];
if (b == 3) {
b = 4;
};
while (b < 10) {
b = b + 1;
};
func doNothing() {
return ;
};
func square(num) {
doNothing();
return (num * num);
};
func add(a, b) {
return (a + b);
};
i = square(2);
j = add(square(2), square(3));
func getNestedFunc(num) {
func nestedMaker(x) {
return (x % num);
};
return nestedMaker;
};
class Person {
age;
firstName;
lastName;
smart;
someFunc;
yearInSchool;
classes;
classKeys;
};
let person =  {
age: 20;
firstName: name;
lastName: "Martin";
smart: true;
someFunc: doNothing();
yearInSchool: (5 - 1);
classes: classes;
classKeys: 
[
403, 
491, 
300, 
301
];
};
let emptyObj =  {
};
person.age = 21;
c = square(b);
if (cool & true) {
f = 8;
} else if (cool) {
f = 9;
} else  {
f = 10;
};
if (9 == 10) {
print("Math broke!");
};
return 0;
Pretty Printed version of the pretty printed version:
let a = 1;
let b = 1 + 1 - a;
let c;
let d, e;
let f, g = 6 - 1;
let h = a + b;
let cool = true;
let name = "Nick";
let classes = 
[
"CS300", 
"CS301", 
"CS403", 
"CS491"
];
if (b == 3) {
b = 4;
};
while (b < 10) {
b = b + 1;
};
func doNothing() {
return ;
};
func square(num) {
doNothing();
return (num * num);
};
func add(a, b) {
return (a + b);
};
i = square(2);
j = add(square(2), square(3));
func getNestedFunc(num) {
func nestedMaker(x) {
return (x % num);
};
return nestedMaker;
};
class Person {
age;
firstName;
lastName;
smart;
someFunc;
yearInSchool;
classes;
classKeys;
};
let person =  {
age: 20;
firstName: name;
lastName: "Martin";
smart: true;
someFunc: doNothing();
yearInSchool: (5 - 1);
classes: classes;
classKeys: 
[
403, 
491, 
300, 
301
];
};
let emptyObj =  {
};
person.age = 21;
c = square(b);
if (cool & true) {
f = 8;
} else if (cool) {
f = 9;
} else  {
f = 10;
};
if (9 == 10) {
print("Math broke!");
};
return 0;
diff -s -q test1.pp.1 test1.pp.2
Files test1.pp.1 and test1.pp.2 are identical

test2:
Original file:
//Written by Nick Martin for CS403 - Programming Languages
//The purpose of test2 is to test robustness of implemenation of commenting (obviously needed)

let a = 1; //lllllllla
let b = 2;
let c = 3;
/*
*
* things get interesting
*
*/    


    let e = 4;    
let f = 10; //cool
// neat

/*
*
*
* let g = 88;
*
*/Pretty Printed version of the original:
let a = 1;
let b = 2;
let c = 3;
let e = 4;
let f = 10;
Pretty Printed version of the pretty printed version:
let a = 1;
let b = 2;
let c = 3;
let e = 4;
let f = 10;
diff -s -q test2.pp.1 test2.pp.2
Files test2.pp.1 and test2.pp.2 are identical

test3:
Original file:
//Written by Nick Martin for CS403 - Programming Languages

//The purpose of test3 is to test functionality of function defs

func doNothing() {
    return;
};

func square(num) {
    doNothing();
    RETURN (num * num);
};

func add(a, b) {
    func subtract(x, y) {
        func multiply(z, w) {
            return (z * w);
        };
        return multiply((x - y), (y - x));
    };
    return subtract((a + a), (b + b));
};

i = square(2);
j = add(square(2), square(3));
Pretty Printed version of the original:
func doNothing() {
return ;
};
func square(num) {
doNothing();
return (num * num);
};
func add(a, b) {
func subtract(x, y) {
func multiply(z, w) {
return (z * w);
};
return multiply((x - y), (y - x));
};
return subtract((a + a), (b + b));
};
i = square(2);
j = add(square(2), square(3));
Pretty Printed version of the pretty printed version:
func doNothing() {
return ;
};
func square(num) {
doNothing();
return (num * num);
};
func add(a, b) {
func subtract(x, y) {
func multiply(z, w) {
return (z * w);
};
return multiply((x - y), (y - x));
};
return subtract((a + a), (b + b));
};
i = square(2);
j = add(square(2), square(3));
diff -s -q test3.pp.1 test3.pp.2
