import java.io.*;
import java.util.*;

public class Bestof5 {
	public static void main ( String [] args ) throws IOException{
		//Takes all the lines that have a perplexity score next to them in output.txt
		//and picks the best of 5.  it saves these in "tmp.txt"
		BufferedReader f = new BufferedReader( new FileReader( "output.txt" ) );
		File file = new File("tmp.txt");
		if( !file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile() );
		BufferedWriter out = new BufferedWriter( fw);
		
		while( f.ready() ){
			double [] scores = new double [5];
			String [] sentences = new String [5];
			int minIndex = -1; double minValue = 99999999;
			String [] ar ;
			for( int i = 0 ; i < 5 ; i++ ){
				sentences[i] = f.readLine();
				ar = sentences[i].split(" ");
				String number = ar[ar.length-1];
				number = number.substring(2,number.length()-1);
				scores[i] = Double.parseDouble( number );
				if( scores[i] < minValue ){
					minValue = scores[i];
					minIndex = i;
				}
			}
			ar = sentences[minIndex].split(" ");
			for( int j = 0 ; j < ar.length - 1 ; j ++ ){
				out.write( ar[j]+" ");
			}
			out.write( "\n");
			out.flush();
			
		}
		out.close();
	}
}