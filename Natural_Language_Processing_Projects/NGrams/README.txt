Peter Schaldenbrand
Homework 2
Dr. Hwa

compiling and running:

Part I&II
The program that executes the requirements of the first two parts is Ngram.java
to compile:
	javac Ngram.java
to run:
	java Ngram <1|2|2s|3|3s> <trainfile> <devfile> <testfile>
the files must be in the same folder as the program.
example of running:
	javac Ngram.java	
	java Ngram 3s train.txt dev.txt test.txt

	
Part III

I have by default implemented part III with a smoothed trigram.  However, if
you edit the code for Extrinsic.java, you'll see that there are 11 other Ngrams
that can be used that I have implemented.  To use one of these, simply take out
the "//" in front of the line with the Ngram you want, then add "//" in front of 
all the ones that you don't want to run.

there are multiple steps to get part III to go.  the first involves Extrinsic.java.
This program makes the Ngram, develops it, and then determines the perplexity of
each of the lines in Holmes.lm_format.questions.txt.  It prints the perplexity to 
the right of each line in a file called output.txt.  "Holmes.lm_format.questions.txt"
MUST be in a folder called "Data"!  Extrinsic opens this text file calling it
"./Data/Holmes.lm_format.questions.txt".

All the training data for Extrinsic must be in a folder entitled "Holmes_Training_Data"
Extrinsic.java opens these txt files calling them for example:
"./Holmes_Training_Data/1MLKD11.txt".  There are many of the project Guttenberg 
files being used but not all of them (That would take a lot of time to run).

to compile:		javac Extrinsic.java
to run:			java Extrinsic

The next part of part III is to pick the best line (lowest perplexity) of output.txt.
this is done using a program I wrote called Bestof5.java.  it takes the lines from
output.txt, then takes the best one from each question and writes it to a file
called "tmp.txt".

to compile: 	javac Bestof5.java
to run:			java Bestof5

The last part of part III is scoring the sentences chosen by my programs.  This is
done using a program that I didn't write called "score.pl".  the first argument of
this script is the text file with the user's answers, and the second argument is 
the actual answers.  The program outputs the score to the standard out.
"Holmes.lm_format.answers.txt" MUST be in the folder with Extrinsic.java.

to run:			perl score.pl tmp.txt Holmes.lm_format.answers.txt

In summary, to do the whole part three; uncomment the gram you want in 
Extrinsic.java, then type;
	javac Extrinsic.java
	java Extrinsic
	javac Bestof5.java
	java Bestof5
	perl score.pl tmp.txt Holmes.lm_format.answers.txt