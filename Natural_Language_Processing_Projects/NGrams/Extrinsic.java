import java.io.*;
import java.util.*;

public class Extrinsic {
	public static void main ( String [] args ) throws IOException{
		//Set up training data
		//Use just one of these
		
		//Trigram t = new Trigram();
		//Unigram t = new Unigram();
		SmoothTrigram t = new SmoothTrigram();
		//SmoothBigram t = new SmoothBigram();
		//Bigram t = new Bigram();
		//ReverseBigram t = new ReverseBigram();
		//SmoothReverseBigram t = new SmoothReverseBigram();
		//ReverseTrigram t = new ReverseTrigram();
		//SmoothReverseTrigram t = new SmoothReverseTrigram();
		//Quadgram t = new Quadgram();
		//SmoothQuadgram t = new SmoothQuadgram();
		
		long start = System.currentTimeMillis();
		
		String [] files = {
							"./Holmes_Training_Data/1BOON10.txt",
							"./Holmes_Training_Data/1BSKT10.txt",
							"./Holmes_Training_Data/1DFRE10.txt",
							"./Holmes_Training_Data/1DINA10.txt",
							"./Holmes_Training_Data/1DONQ10.txt",
							"./Holmes_Training_Data/1MLKD11.txt",
							"./Holmes_Training_Data/1RBNH10.txt",
							"./Holmes_Training_Data/2DFRE10.txt",
							"./Holmes_Training_Data/2DINA10.txt",
							"./Holmes_Training_Data/2MROW10.txt",
							"./Holmes_Training_Data/2RBNH10.txt",
							"./Holmes_Training_Data/2TALE10.txt",
							"./Holmes_Training_Data/3BOAT10.txt",
							"./Holmes_Training_Data/3DFRE10.txt",
							"./Holmes_Training_Data/3ELPH10.txt",
							"./Holmes_Training_Data/04TOM10.txt",
							"./Holmes_Training_Data/4DFRE10.txt",
							"./Holmes_Training_Data/05TOM10.txt",
							"./Holmes_Training_Data/5DFRE10.txt",
							"./Holmes_Training_Data/6DFRE10.txt",
							"./Holmes_Training_Data/07WOZ10.txt",
							"./Holmes_Training_Data/7GABL10.txt",
							"./Holmes_Training_Data/08WOZ10.txt",
							"./Holmes_Training_Data/09WOZ10.txt",
							"./Holmes_Training_Data/10EVM10.txt",
							"./Holmes_Training_Data/10WOZ10.txt",
							"./Holmes_Training_Data/11WOZ10.txt",
							"./Holmes_Training_Data/12TOM10.txt",
							"./Holmes_Training_Data/12WOZ10.txt",
							"./Holmes_Training_Data/14WOZ10.txt",
							"./Holmes_Training_Data/19TOM10.txt",
							"./Holmes_Training_Data/21TOM10.txt",
							"./Holmes_Training_Data/39STP10.txt",
							"./Holmes_Training_Data/ACHOE10.txt",
							"./Holmes_Training_Data/ACRDI10.txt",
							"./Holmes_Training_Data/ADAMB10.txt"
		};
		String line;
		for( int i = 0 ; i < files.length ; i++ ){
			TextProcessor tp = new TextProcessor();
			String trainfile = files[i];
			if( tp.process( trainfile ) ){
				trainfile = trainfile.substring(0,trainfile.length()-4);
				trainfile = trainfile + "-out.txt";
			}
			t.make( trainfile );
			
		}
		long time = System.currentTimeMillis() - start;
		start = System.currentTimeMillis();
		time = time /1000;
		System.out.println("Time to do make() was " +time+" seconds.");
		
		
		t.develop( "./Holmes_Training_Data/2DINA10.txt");
		
		time = System.currentTimeMillis() - start;
		time = time /1000;
		System.out.println("Time to do develop() was " + time + " seconds.");
		
		BufferedReader quest = new BufferedReader( new FileReader( "./Data/Holmes.lm_format.questions.txt" ) );
		File file = new File( "output.txt");
		if( !file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile() );
		BufferedWriter out = new BufferedWriter( fw);
		while( quest.ready() ){
			String question;
			
			question = quest.readLine();
			String perplex = t.perplexity( question );
			out.write( question );
			out.write( "\t-"+perplex.substring(0,5) + "\n" );
			out.flush();
		}
		out.close();
	
	}
}