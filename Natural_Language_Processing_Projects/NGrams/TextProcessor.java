import java.io.*;
import java.util.*;

public class TextProcessor {
	//Takes a normal text file and then processes it so that it is 
	//tokenized and everything.
	BufferedWriter out;
	
	public TextProcessor(){
		
	}
	public TextProcessor( String fileName ) throws IOException{
		//process( fileName );
	}
	public boolean process( String fileName ) throws IOException{
		BufferedReader f = new BufferedReader( new FileReader( fileName ) );
		File file = new File( fileName.substring(0,fileName.length()-4)+"-out.txt");
		if( !file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile() );
		out = new BufferedWriter( fw);
		
		boolean test = true;
		while( f.ready() ){
			String line = f.readLine();
			String [] ar = line.split(" ");
			if( ar.length == 0 ){ continue; }
			if( ar[0].equals("<s>") && test == true ){
				return false; //file is already processed
			}
			else{ test = false; }
			
			for( int i = 0; i < ar.length ; i++ ){
				String word = remove( ar[i] );
				if( word.length() == 0 ){
					continue;
				}
				char last = 'a';
				if( word.length() >1 ){
					last =  word.charAt(word.length()-1);
				}
				if( last=='!'||last=='?' || (last=='.' && isPeriod(word)) ){
					word = word.substring(0,word.length()-1);
					out.write( word+" </s>\n<s> " );
				}
				else{
					out.write( word+" ");
				}
				out.flush();
			}
			
		}
		return true;
	}
	private boolean isPeriod( String s ){
		//determine if the string ending in a period is the ending
		//of a sentence or not
		ArrayList<String> punct = new ArrayList<String>();
		punct.add("mr.");
		punct.add("mrs.");
		punct.add("dr.");
		punct.add("etc");
		punct.add("mz.");
		punct.add("ms.");
		punct.add("i.e.");
		punct.add("a.d.");
		punct.add("b.c.");
		punct.add("d.c.");
		punct.add("est.");
		punct.add("ft.");
		punct.add("gen.");
		punct.add("inc.");
		punct.add("jan.");
		punct.add("feb.");
		punct.add("mar.");
		punct.add("apr.");
		punct.add("may.");
		punct.add("jun.");
		punct.add("jul.");
		punct.add("aug.");
		punct.add("sept.");
		punct.add("sep.");
		punct.add("oct.");
		punct.add("nov.");
		punct.add("dec.");
		punct.add("mt.");
		punct.add("rev.");
		punct.add("sgt.");
		punct.add("univ.");
		punct.add("vol.");
		punct.add("vs.");
		punct.add("yd.");
		punct.add("jr.");
		punct.add("sr.");
		punct.add("min.");
		punct.add("m.");
		punct.add("a.m.");
		punct.add("p.m.");
		punct.add("ave.");
		punct.add("st.");
		punct.add("rd.");
		punct.add("pvt.");
		punct.add("blvd.");
		
		if(	punct.contains( s.toLowerCase()) ){
			return false;
		}
		return true;
	}
	private String remove( String s ){
		//remove unnecessary punctuation
		ArrayList<String> punct = new ArrayList<String>();
		punct.add(",");
		punct.add("\\");
		punct.add("\"");
		punct.add(":");
		punct.add(";");
		punct.add("<");
		punct.add(">");
		punct.add("{");
		punct.add("}");
		punct.add("(");
		punct.add(")");
		punct.add("+");
		punct.add("=");
		punct.add("$");
		punct.add("%");
		punct.add("/");
		punct.add("#");
		punct.add("^");
		punct.add("*");
		punct.add("|");
		punct.add("-");
		
		String ret = "";
		char[] ar = s.toCharArray();
		for( int i = 0 ; i < ar.length ; i++){
			if( punct.contains(ar[i]+"") == false ){
				ret = ret + ar[i];
			}
		}
		return ret;
	}
	
}