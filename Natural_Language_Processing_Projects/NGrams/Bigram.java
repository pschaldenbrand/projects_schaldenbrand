import java.io.*;
import java.util.*;
import java.math.*;

public class Bigram implements NgramInterface{
	
	public HashMap<String,HashMap<String,Integer> > gram;
	public HashMap<String,Integer> columnCount;
	public int count;
	
	public Bigram(){
		gram = new HashMap<String,HashMap<String,Integer> >();
		count = 0;
		columnCount = new HashMap<String,Integer>();
	}
	public void make( String filename ) throws IOException{
		//takes a file that is all tokenized
		BufferedReader f = new BufferedReader( new FileReader( filename ) );
		
		while( f.ready()){
			String line = f.readLine();
			String [] tokens = line.split(" ");
			//handle first case
			if( gram.containsKey(tokens[0]) == false ){
				gram.put( tokens[0], new HashMap<String,Integer>());
				columnCount.put( tokens[0], 0 );
			}
			String prev = tokens[0];
			for( int i = 1; i < tokens.length; i++ ){
				String next = tokens[i];
				
				if( gram.containsKey(prev) == false ){
					gram.put(prev, new HashMap<String,Integer>());
					columnCount.put( prev, 0 );
				}
				
				HashMap<String,Integer> column = gram.get( prev );
				if( column.containsKey(next) == false ){
					column.put( next, 1 );
				}
				else{
					column.put(next, column.get(next) + 1);
				}
				
				count++;
				columnCount.put( prev, columnCount.get(prev) + 1 );
				prev = next;
			}
		}
		
		
		gram.put("<Unk>", new HashMap<String,Integer>() );
		gram.get("<Unk>").put( "<Unk>", 1 );
		columnCount.put("<Unk>",1);
		count++;
		
		Set<String> temp = gram.keySet();
		Object [] keys = temp.toArray();
		
		for( int i = 0; i < gram.size() ; i++){
			//add <Unk> as a value for all the columns.  make the
			//value equal to the amount of values in the column
			String key = (String)keys[i];
			gram.get(key).put("<Unk>", gram.get(key).size() );
			count += gram.get(key).size();
			columnCount.put( key, gram.get(key).size() * 2 );
		}
	}
	public String perplexity( String line){
		MathContext mc = new MathContext( 128 );
		String [] ar = line.split(" ");
		
		BigDecimal perplex = new BigDecimal(1 , mc );
		String prev = ar[0];
		String next;
		for( int i = 1 ; i < ar.length ; i++ ){
			next = ar[i];
			BigDecimal curr = wordPerplex( prev, next );
			perplex = perplex.multiply( curr, mc );
			prev = next;
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
	private BigDecimal wordPerplex( String prev, String next ){
		HashMap<String,Integer> column;
		int c;
		if( gram.containsKey(prev) ){
			column = gram.get(prev);
			c = columnCount.get(prev);
		}
		else{ 
			column = gram.get("<Unk>"); 
			c = columnCount.get("<Unk>"); 
		}
		int times = 0;
		if( column.containsKey(next) ){
			times = column.get(next);
		}
		else{ times = column.get("<Unk>"); }
		if( times == 0 ){ times = 1; System.out.println("No data on this probability."); }
		BigDecimal ret = new BigDecimal( times );
		MathContext mc = new MathContext( 128 );
		ret = ret.divide( new BigDecimal(c,mc) , mc );
		return ret;
	}
	public void develop( String fn )throws IOException{
		return;
	}
	public void develop1( String fn )throws IOException{
		return;
	}
	public void view(){
		Set<String> temp = gram.keySet();
		Object[] ColumnKeys = temp.toArray();
		
		for( int i = 0; i < ColumnKeys.length ; i++ ){
			String ColumnKey = (String) ColumnKeys[i];
			HashMap<String,Integer> column = gram.get(ColumnKey);
			
			temp = column.keySet();
			Object[] rowKeys = temp.toArray();
			for( int j = 0; j < rowKeys.length ; j++ ){
				String rowKey = (String) rowKeys[j];
				System.out.print( "Pr(\t"+rowKey+"  \t| "+ColumnKey+" ) =\t");
				System.out.println( column.get(rowKey) +"\t/ " +count);
				
			}
		}
		
	}
}