                           logicalexpression README

Introduction
--------------------

This program specifies an interface for a logical expression and implements it
in the LogicalExpression class.  The logical expression is constructed from a
string, and is converted to a list of tokens.  The expression can be checked for
logical properties such as validity, satisfiability, contingency, as well as
logical relations with other expressions such as entailment and equivalence.

Specification
--------------------

This program meets the requirements presented by the assignment.  The interface,
LogicalExpressionInterface, requires all the methods specified in the
assignment.  These include methods to check for validity, satisfiability and
contingency, as well as entailment and equivalence to other expressions.  The
class, LogicalExpression, implements this interface successfully.  In addition
to meeting the requirements of the interface, the class also contains a
constructor to take a string and parse it into a logical expression, as well as
methods to evaluate the expression for a certain set of truth values.

Errors
--------------------

There are no notable errors in the program that have been found thus far.  The
program has performed as expected for all test cases.

Overview of Code
--------------------

Broadly speaking, this code tries to make use of multiple methods effectively
to make the program as efficient as possible, as well as more understandable.
A LogicalExpression object is constructed from a string, which is parsed into
a ArrayList of tokens.  This list of tokens is "shunted" using the shunting yard
algorithm, which reorders the tokens into postfix notation.  This allows for
easy evaluation given a set of truth values, which is done using a stack.

The valid, satisfiable and contingent methods generate all possible truth values
using bit shifting and check to see what the sentence evaluates to. The entails
and equivalent methods create new sentences by joining two original sentences,
and then check if those sentences are valid.

Major Challenges
--------------------

This project was not too challenging once I found some algorithms to base my
code off of.  I was able to use some new things, such as enums, in my code, so
that was a good learning experience.

Acknowledgements
--------------------

As always, I would like to thank my family for supporting me while I work on
time-consuming projects such as this.