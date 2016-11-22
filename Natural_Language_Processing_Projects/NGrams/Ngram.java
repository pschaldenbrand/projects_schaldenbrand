import java.io.*;
import java.util.*;

public class Ngram {
	public static void main ( String [] args ) throws IOException{
		//Intrinsic Evaluation
		String trainfile = args[1];
		String devfile = args[2];
		String testfile = args[3];
		String m = args[0];
		
		TextProcessor t = new TextProcessor( );
		if( t.process( trainfile ) ){
			trainfile = trainfile.substring(0,trainfile.length()-4);
			trainfile = trainfile + "-out.txt";
		}
		
		NgramInterface ngram;
		if( m.equals("1") ){
			ngram = new Unigram();
		}
		else if( m.equals("2") ){
			ngram = new Bigram();
		}
		else if( m.equals("3") ){
			ngram = new Trigram();
		}
		else if( m.equals("2s") ){
			ngram = new SmoothBigram();
		}
		else if( m.equals("3s") ){
			ngram = new SmoothTrigram();
		}
		else{ 
			System.out.println("First Argument is incorrect. try <1|2|2s|3|3s>\nDefault = Unigram");
			ngram = new Unigram();
		}
		
		ngram.make( trainfile );
		ngram.develop1( devfile );
		
		BufferedReader test = new BufferedReader( new FileReader( testfile ) );
		System.out.println("Perplex.:");
		while( test.ready() ){
			String line = test.readLine();
			String perplex = ngram.perplexity( line );
			System.out.println( perplex.substring(0,6)+":\t" + line );
		}
	}
}