import java.io.*;
import java.util.*;
import java.math.*;

public class SmoothQuadgram implements NgramInterface{
	//Implemented using a Trigram, Unigram, and Quadgram
	public Trigram trigram;
	public Quadgram quadgram;
	public Unigram unigram;
	public double lamdaTri;
	public double lamdaQuad;
	public double lamdaUni;
	
	public SmoothQuadgram(){
		trigram = new Trigram();
		quadgram = new Quadgram();
		unigram = new Unigram();
		lamdaTri = 0.5;
		lamdaQuad = 0.3;
		lamdaUni = 0.2;
	}
	public void make( String fileName ) throws IOException{
		trigram.make(fileName);
		quadgram.make(fileName);
		unigram.make(fileName);
	}
	public String perplexity( String line ){
		
		BigDecimal t = new BigDecimal( trigram.perplexity( line ) );
		BigDecimal b = new BigDecimal( quadgram.perplexity( line ) );
		BigDecimal u = new BigDecimal( unigram.perplexity( line ) );
		t = t.multiply( new BigDecimal( lamdaTri ) );
		b = b.multiply( new BigDecimal( lamdaQuad ) );
		u = u.multiply( new BigDecimal( lamdaUni ) );
		BigDecimal total = b.add( u );
		total = total.add( t );
		return total.toString();
	}
	public void develop1( String fn )throws IOException{
		BufferedReader f = new BufferedReader( new FileReader( fn ) );
		String devLine = "";
		while( f.ready() ){
			devLine = devLine + " " + f.readLine();
		}
		double [] val = {0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9};
		double [][] score = new double [9][9];
		int mini = -1;
		int minj = -1;
		double minVal = 9999999;
		
			for( int i = 0; i < val.length ; i++ ){
				lamdaTri = val[i];
				for( int j = 0; j < val.length; j++ ){
					lamdaQuad = val[j];
					if( lamdaQuad + lamdaTri >= 1.0 ){
						continue;
					}
					lamdaUni = 1.0-(lamdaQuad+lamdaTri);
					String temp = this.perplexity( devLine );
					BigDecimal tempBD = new BigDecimal( temp );
					double tempd= tempBD.doubleValue();
					if( tempd < minVal ){
						mini = i;
						minj = j;
						minVal = tempd;
					}
				}
			}
		lamdaTri = val[mini];
		lamdaQuad = val[minj];
		lamdaUni = 1- ( lamdaTri + lamdaQuad );
	}
	public void develop( String fn )throws IOException{
		BufferedReader f = new BufferedReader( new FileReader( fn ) );
		String devLine = "";
		BufferedReader q = new BufferedReader( new FileReader( "./Data/Holmes.lm_format.questions.txt" ) );
		BufferedReader a = new BufferedReader( new FileReader( "Holmes.lm_format.answers.txt" ) );
		double [] val = {0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9};
		int [][] score = new int [9][9];
		int mini = -1;
		int minj = -1;
		double minVal = 9999999;
		for( int l = 0 ; l < 520 ; l ++ ){
			String [] lines = new String [5];
			for( int k = 0; k < 5 ; k++ ){
				lines[k] = q.readLine();
			}
			String correctLine = a.readLine();
			for( int i = 0; i < val.length ; i++ ){
				lamdaTri = val[i];
				for( int j = 0; j < val.length; j++ ){
					
					lamdaQuad = val[j];
					if( lamdaQuad + lamdaTri >= 1.0 ){
						continue;
					}
					lamdaUni = 1.0-(lamdaQuad+lamdaTri);
					minVal = 9999999;
					boolean correct = false;
					
					String lowLine = "";
					for( int m = 0; m < 5 ; m++ ){
						devLine = lines[m];
						String temp = this.perplexity( devLine );
						BigDecimal tempBD = new BigDecimal( temp );
						double tempd= tempBD.doubleValue();
						if( tempd < minVal ){
							//mink = k;
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
						score[i][j]+=1;
					}
				}
			}
		}
		int maxScore = 0;
		for( int n = 0 ; n < 9; n++ ){
			for( int o = 0; o < 9 ; o++ ){
				if( score[n][o] > maxScore ){
					mini = n; minj = o;
					maxScore = score[n][o];
				}
			}
		}
				
		lamdaTri = val[mini];
		lamdaQuad = val[minj];
		lamdaUni = 1- ( lamdaTri + lamdaQuad );
	}
	public void view(){
		trigram.view();
		quadgram.view();
		unigram.view();
	}
}