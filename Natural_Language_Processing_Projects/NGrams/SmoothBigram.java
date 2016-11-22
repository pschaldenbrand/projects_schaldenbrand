import java.io.*;
import java.util.*;
import java.math.*;

public class SmoothBigram implements NgramInterface{
	public Bigram bigram;
	public Unigram unigram;
	public double lamdaBi;
	public double lamdaUni;
	
	public SmoothBigram(){
		bigram = new Bigram();
		unigram = new Unigram();
		lamdaBi = 0.7;
		lamdaUni = 0.3;
	}
	public void make( String fileName ) throws IOException{
		bigram.make(fileName);
		unigram.make(fileName);
	}
	public String perplexity( String line ){
		//Returns the perplexity of the input string
		BigDecimal b = new BigDecimal( bigram.perplexity( line ) );
		BigDecimal u = new BigDecimal( unigram.perplexity( line ) );
		b = b.multiply( new BigDecimal( lamdaBi ) );
		u = u.multiply( new BigDecimal( lamdaUni ) );
		BigDecimal total = b.add( u );
		return total.toString();
	}
	public void develop1( String fn ) throws IOException{
		//sets the lamda values based on the input string.
		BufferedReader f = new BufferedReader( new FileReader( fn ) );
		String devLine = "";
		while( f.ready() ){
			devLine = devLine + " " + f.readLine();
		}
		double [] val = {0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9};
		double [] perplexities = new double [9];
		for( int i = 0; i < val.length ; i ++ ){
			lamdaBi = val[i];
			lamdaUni = 1-lamdaBi;
			String temp = this.perplexity( devLine );
			BigDecimal tempBD = new BigDecimal( temp );
			perplexities[i] = tempBD.doubleValue();
		}
		double minValue = 9999999;
		int minIndex = -1;
		for( int i = 0; i < perplexities.length ; i++ ){
			if( perplexities[i] < minValue ){
				minValue = perplexities[i];
				minIndex = i;
			}
		}
		lamdaBi = val[minIndex];
		lamdaUni = 1 - lamdaBi;
	}
	public void develop( String fn ) throws IOException{
		//creates lamda values for the Sherlock Holmes question and answers
		BufferedReader f = new BufferedReader( new FileReader( fn ) );
		BufferedReader q = new BufferedReader( new FileReader( "./Data/Holmes.lm_format.questions.txt" ) );
		BufferedReader a = new BufferedReader( new FileReader( "Holmes.lm_format.answers.txt" ) );
		
		String devLine = "";
		
		double [] val = {0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9};
		double [] perplexities = new double [9];
		double minVal = 9999999;
		int [] score = new int [9];
		
		for( int l = 0 ; l < 500 ; l++ ){
			//go through each question in the development set
			String [] lines = new String [5];
			for( int k = 0; k < 5 ; k++ ){
				lines[k] = q.readLine();
			}
			String correctLine = a.readLine();
			
			minVal = 999999;
			for( int i = 0; i < val.length ; i ++ ){
				lamdaBi = val[i];
				lamdaUni = 1-lamdaBi;
				String lowLine = "";
				for( int j = 0; j<5 ; j++ ){
					devLine = lines[j];
					String temp = this.perplexity( devLine );
					BigDecimal tempBD = new BigDecimal( temp );
					double tempd = tempBD.doubleValue();
					if( tempd < minVal ){
						minVal = tempd;
						lowLine = devLine;
					}
				}
				int size = 0;
				if( lowLine.length() < correctLine.length() ){
					size = lowLine.length();
				}
				else{ size = correctLine.length() ;}
				if( lowLine.substring(0,size-2).equals( correctLine.substring(0,size-2) ) ){
					score[i]+=1;
				}
			}
		}
		int maxVal = 0;
		int maxIndex = -1;
		for( int n = 0; n < 9 ; n++ ){
			if( score[n] > maxVal){
				maxIndex = n;
				maxVal = score[n];
			}
		}
		
		lamdaBi = val[maxIndex];
		lamdaUni = 1 - lamdaBi;
	}
	
	public void view(){
		bigram.view();
		unigram.view();
	}
}