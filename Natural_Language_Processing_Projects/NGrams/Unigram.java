import java.io.*;
import java.util.*;
import java.math.*;

public class Unigram implements NgramInterface{
	
	public HashMap<String,Integer> unigram;
	public int count;
	
	public Unigram(){
		unigram = new HashMap<String,Integer>();
	}
	public void make( String filename ) throws IOException{
		BufferedReader f = new BufferedReader( new FileReader( filename ) );
		
		while( f.ready()){
			String line = f.readLine();
			String [] tokens = line.split(" ");
			for( int i = 0; i < tokens.length; i++ ){
				String word = tokens[i];
				if( unigram.containsKey(word) == false ){
					unigram.put(word, 1);
				}
				else {
					unigram.put( word, unigram.get(word)+1 );
				}
				count++;
			}
		}
		unigram.put("<Unk>", unigram.size() );
		count += unigram.size();
	}
	public String perplexity(String line ){
		MathContext mc = new MathContext( 128 );
		String [] ar = line.split(" ");
		BigDecimal perplex;
		if( unigram.containsKey(ar[0] ) ){
			perplex = new BigDecimal( unigram.get(ar[0]) , mc );
		}
		else{ perplex = new BigDecimal( 1, mc ); }
		perplex = perplex.divide( new BigDecimal( count , mc ) , mc );
		for( int i = 1 ; i < ar.length ; i++ ){
			BigDecimal num;
			if( unigram.containsKey( ar[i] )){
				num = new BigDecimal( unigram.get( ar[i]) , mc );
			}
			else{ num = new BigDecimal( unigram.get("<Unk>") ); }
			num = num.divide( new BigDecimal( count, mc ) , mc );
			perplex = perplex.multiply( num );
		}
		
		BigDecimal one = new BigDecimal(1);
		perplex = one.divide( perplex , mc );
		//(perplexity)^(1/N) == (perp x 10^64)^(1/N) / 10^(1/N)^64
		//Scale is used to make the perplexity into a larger number
		BigDecimal scale = new BigDecimal(10);
		scale = scale.pow(64 , mc);
		perplex = perplex.multiply( scale , mc );
		//Val is the double value of perplexity used to take the root of it
		double val = perplex.doubleValue();
		val = Math.pow( val, (1.0/ar.length) );
		perplex = new BigDecimal( val, mc );
		//div is used to scale the perplexity back down
		double div = Math.pow( 10.0, (1.0/ar.length) );
		scale = new BigDecimal( div, mc );
		scale = scale.pow( 64, mc );
		perplex = perplex.divide( scale, mc );
		return perplex.toString();
		
	}
	public void develop( String fn )throws IOException{
		return;
	}
	public void develop1( String fn )throws IOException{
		return;
	}
	public void view(){
		Set<String> set = unigram.keySet();
		Object[] ar = set.toArray();
		for( int i = 0; i < ar.length ; i++ ){
			String s = (String)ar[i];
			System.out.println(s+"\t"+unigram.get(s));
		}
	}
}