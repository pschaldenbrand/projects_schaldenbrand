import java.io.*;
import java.util.*;
import java.math.*;

public class TestGram {
	public static void main ( String [] args ) throws IOException{
		//String test = "<s> it was and is Yeezus that is really great and things for such I like </s>";
		//String filename = "YeezusReview-out.txt";
		String test = "<s> a b b a </s>";
		String filename = "abba.txt";
		Unigram u = new Unigram();
		u.make(filename);
		String s = u.perplexity(test );
		System.out.println( "Unigram Perplexity =\t"+s.substring(0,10) );
		
		
		
		Bigram bi = new Bigram();
		bi.make(filename);
		s = bi.perplexity(test );
		System.out.println( "Bigram Perplexity =\t"+s.substring(0,10) );
		
		ReverseBigram rbi = new ReverseBigram();
		rbi.make(filename);
		s = rbi.perplexity(test );
		System.out.println( "ReverseBigram Perplexity =\t"+s.substring(0,10) );
		
		
		Trigram tri = new Trigram();
		tri.make(filename);
		s = tri.perplexity( test );
		System.out.println( "Trigram Perplexity =\t" + s.substring(0,10));
		
		SmoothBigram sb = new SmoothBigram();
		sb.make( filename );
		s = sb.perplexity( test );
		System.out.println( "SmoothBigram Perp. =\t" + s.substring(0,10));
		sb.develop1( "toydev.txt" );
		s = sb.perplexity( test );
		System.out.println( "SmoothBigram Perp. =\t" + s.substring(0,10));
		
		SmoothTrigram tb = new SmoothTrigram();
		tb.make( filename );
		s = tb.perplexity( test );
		System.out.println( "SmoothTrigram Perp. = \t" + s.substring(0,10));
		tb.develop1("toydev.txt");
		s = tb.perplexity( test );
		System.out.println( "SmoothTrigram Perp. = \t" + s.substring(0,10));
		
		//TextProcessor t = new TextProcessor( "YeezusReview.txt" );
	}
}