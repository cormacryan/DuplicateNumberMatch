# DuplicateNumberMatch

Application to detect and print out duplicate numbers stored in an input file based on limited memory 
and processing available on the machine the application is being run on.  

Input either takes 1 argument which is a file name containing the list of numbers to sort or no arguments
in which case a temp file will be created with some random numbers for processing.
 
Run against a random generated text file of numbers
java -jar build/jar/NumberMatcher.jar

Run against a given passed in file of numbers
java -jar build/jar/NumberMatcher.jar numbers.txt

Run a test scenario against a small and large number set.
java -jar build/jar/NumberMatcher.jar runtest
