import java.io.*;
import java.util.*;
import java.math.*;

public class Trigram implements NgramInterface{
	
	public HashMap<String,HashMap<String,Integer> > gram;
	public int count;
	public HashMap<String, Integer> columnCount;
	
	public Trigram(){
		gram = new HashMap<String,HashMap<String,Integer> >();
		count = 0;
		columnCount = new HashMap<String,Integer>();
	}
	public void make( String filename ) throws IOException{
		//takes a file that is all tokenized and makes the Ngram
		BufferedReader f = new BufferedReader( new FileReader( filename ) );
		
		while( f.ready()){
			String line = f.readLine();
			String [] tokens = line.split(" ");
			//handle first case
			if( gram.containsKey(tokens[0]) == false ){
				gram.put( tokens[0], new HashMap<String,Integer>());
				columnCount.put( tokens[0], 0 );
			}
			else{ columnCount.put( tokens[0], columnCount.get(tokens[0])+1); }
			HashMap<String,Integer> firstCol = gram.get(tokens[0]);
			if( tokens.length < 2 ){ 
				continue; //only one word on this line
			}
			if( firstCol.containsKey( tokens[1] ) == false ){
				firstCol.put( tokens[1], 1);
			}
			else{
				firstCol.put( tokens[1], firstCol.get(tokens[1])+1 );
			}
			String prev1 = tokens[0];
			String prev2 = tokens[1];
			
			for( int i = 2; i < tokens.length; i++ ){
				String next = tokens[i];
				String prevword = prev1 + " " + prev2;
				
				if( gram.containsKey(prevword) == false ){
					gram.put(prevword, new HashMap<String,Integer>());
					columnCount.put( prevword, 0 );
				}
				
				HashMap<String,Integer> column = gram.get( prevword );
				if( column.containsKey(next) == false ){
					column.put( next, 1 );
				}
				else{
					column.put(next, column.get(next) + 1);
				}
				
				columnCount.put( prevword, columnCount.get(prevword) + 1 );
				count++;
				prevword = prev2+" "+next;
				prev1 = prev2;
				prev2 = next;
			}
		}
		
		//for when the previous word is unknown
		gram.put("<Unk>", new HashMap<String,Integer>() );
		gram.get("<Unk>").put( "<Unk>", 1 );
		count++;
		columnCount.put("<Unk>",1);
		
		Set<String> temp = gram.keySet();
		Object [] keys = temp.toArray();
		
		for( int i = 0; i < gram.size() ; i++){
			//add <Unk> as a value for all the columns.  make the
			//value equal to the amount of values in the column
			String key = (String)keys[i];
			
			gram.get(key).put("<Unk>", 1 );
			count += 1;
			columnCount.put( key, gram.get(key).size() + 1 );
		}
	}
	public String perplexity( String line ){
		MathContext mc = new MathContext( 128 );
		String [] ar = line.split(" ");
		
		BigDecimal perplex = new BigDecimal(1 , mc );
		String prev1 = ar[0];
		String prev2 = ar[1];
		BigDecimal curr = wordPerplex( prev1, ar[1] );
		perplex = perplex.multiply( curr, mc );
		
		
		String next;
		for( int i = 2 ; i < ar.length ; i++ ){
			next = ar[i];
			curr = wordPerplex( prev1+" "+prev2, next );
			perplex = perplex.multiply( curr, mc );
			prev1 = prev2;
			prev2 = next;
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
	private BigDecimal wordPerplex ( String prev, String next ){
		//Helper function to find perplexity. finds the perplexity
		//of the input words
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
		if( c == 0 ){ c = 1; System.out.println("times = "+times+prev); }
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
				System.out.print( "Pr( "+rowKey+"  \t| "+ColumnKey+" ) =\t");
				System.out.println( column.get(rowKey) +"\t/ " +count);
				
			}
		}
		
	}
}