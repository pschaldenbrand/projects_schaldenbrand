import java.io.*;
import java.util.*;
import java.math.*;

public class SmoothReverseBigram implements NgramInterface{
	//implemented using a bigram and reverse bigram
	public Bigram bigram;
	public ReverseBigram revbigram;
	public double lamdaBi;
	public double lamdaRev;
	
	public SmoothReverseBigram(){
		bigram = new Bigram();
		revbigram = new ReverseBigram();
		lamdaBi = 0.7;
		lamdaRev = 0.3;
	}
	public void make( String fileName ) throws IOException{
		bigram.make(fileName);
		revbigram.make(fileName);
	}
	public String perplexity( String line ){

		BigDecimal b = new BigDecimal( bigram.perplexity( line ) );
		BigDecimal u = new BigDecimal( revbigram.perplexity( line ) );
		b = b.multiply( new BigDecimal( lamdaBi ) );
		u = u.multiply( new BigDecimal( lamdaRev ) );
		BigDecimal total = b.add( u );
		return total.toString();
	}
	public void develop( String fn )throws IOException{
		return;
	}
	public void develop1( String fn )throws IOException{
		return;
	}
	public void view(){
		return;
		
	}
}