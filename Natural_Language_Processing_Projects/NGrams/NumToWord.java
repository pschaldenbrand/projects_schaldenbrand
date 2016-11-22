//PETER SCHALDENBRAND
//to compile:
//javac NumToWord.java
//to run:
//java NumToWord example.txt

import java.io.*;
import java.util.*;

public class NumToWord {
	public static void main ( String [] args ) throws IOException{
		
		BufferedReader f = new BufferedReader( new FileReader( args[0] ) );
		File out = new File( "output.txt");
		BufferedWriter w = new BufferedWriter(new FileWriter( out.getAbsoluteFile() ) );
		//go through all of the text file
		while( f.ready() ){
			String [] t = f.readLine().split(" ");
			// go through each word of the file
			for( int i = 0; i < t.length ; i++ ){
				if( t[i].length() == 0 ){ continue; }
				
				if( t[i].equals("$")){			//its some cash muhney
					if( (i+2) < t.length ){
						if( t[i+2].toLowerCase().equals("thousand") ||
								t[i+2].toLowerCase().equals("million") ||
								t[i+2].toLowerCase().equals("billion") ){
							w.write( toWord(t[i+1])+t[i+2]+" dollars " );
							i = i + 1;
						}
						else{ w.write( toWord(t[i+1])+ " dollars " ); }
						i = i+1;
					}
					else{ w.write( toWord( t[i+1])+ " dollars ");
						i++;
					}
				}
				else if( t[i].charAt(0) == '$' ){
					String jNum = t[i].substring(1,t[i].length() );
					if( (i+1) < t.length ){
						if( t[i+1].toLowerCase().equals("thousand") ||
								t[i+1].toLowerCase().equals("million") ||
								t[i+1].toLowerCase().equals("billion") ){
							w.write( toWord( jNum )+t[i+1]+" dollars " );
							i++;
						}
						else{  w.write( toWord(jNum)+ " dollars " ); }
					}
					else{ w.write( toWord(jNum) + " dollars ");}
				}
				//see if it's a regular old number
				else if( containsNum( t[i] )){
					if( t.length > i+1 ){
						if( containsNum( t[i+1] )){ // it's a whol number before a fraction
							w.write( toWord(t[i])+"and " );
						}
						else{ w.write( toWord(t[i]) +" ");}
					}
					else{ w.write( toWord(t[i])+" "	);}
				}
				//check if it's a month or date
				else if( month(t[i])){
					String s1 = "";
					String s2 = ""; 
					String s3 = "";
					String s4 = "";
					if( (i+3) < t.length && isANumber(t[i+3]) ){  // there are 4 words as part of the date
						s1 = t[i]; s2 = t[i+1]; s3 = t[i+2]; s4 = t[i+3];
						w.write( date(s1,s2,s3,s4) +" " );
						i = i+3;
					}
					if( (i+2) < t.length && isANumber(t[i+2]) ){	//3 words
						s1 = t[i]; s2 = t[i+1]; s3 = t[i+2];
						w.write( date(s1,s2,s3, s4) +" " );
						i = i+2;
					}
					else if( (i+1) < t.length && isANumber(t[i+1]) ){ //2 words
						s1 = t[i]; s2 = t[i+1];
						w.write( date( s1, s2, s3, s4 ) +" " );
						i = i + 1;
					}
				}
				//see if it's a percent
				else if ( t[i].equals("%") ){
					w.write( "percent " );
				}
				else{ w.write( t[i]+ " "); } // it's not a number or special case
				w.flush();
			}
			w.write("\n");
		}
		w.flush();
		w.close();
	}
	/*
	convert the year to a word
	i.e. 1995 turns to nineteen ninety five
	not nine thousand nine hundred ninety five
	*/
	public static String yearToWord( String s ){
		if( s.length() != 4 ){
			//not a year
			return "";
		}
		if( s.substring(0,2).equals("20")){	//the two thousands will be normal
			return( toWord( s ) );
		}
		return ( toWord( s.substring(0,2))+" "+toWord(s.substring(2,4)));
	}
	/*
	convert a date to a string
	*/
	public static String date( String s1, String s2, String s3, String s4 ){
		HashMap<String,String> months = new HashMap<String, String>();
		if( true ){
			months.put("jan","January");
			months.put("feb","February");
			months.put("mar","March");
			months.put("apr","April");
			months.put("may","May");
			months.put("jun","June");
			months.put("jul","July");
			months.put("aug","August");
			months.put("sept","September");
			months.put("sep", "September");
			months.put("oct","October");
			months.put("nov","November");
			months.put("dec","December");
			
			months.put("jan.","January");
			months.put("feb.","February");
			months.put("mar.","March");
			months.put("apr.","April");
			months.put("may.","May");
			months.put("jun.","June");
			months.put("jul.","July");
			months.put("aug.","August");
			months.put("sept.","September");
			months.put("sep.", "September");
			months.put("oct.","October");
			months.put("nov.","November");
			months.put("dec.","December");
		}
		//make abreviations the full word
		if( months.containsKey(s1.toLowerCase())){
			s1 = months.get(s1.toLowerCase());
		}
		StringBuilder word = new StringBuilder("");
		
		//month day , year
		if( s4.length() != 0 && isANumber(s4) ){
			word.append( s1+" "+place(s2)+", "+yearToWord(s4));
		}
		//month day year or month , day or month , year
		else if( s3.length() != 0 && isANumber(s3)){
			if( s2.equals(",") ){
				if( s3.length() < 4 ){
					word.append( s1 +" "+ place(s3) );
				}
				else{
					word.append( s1 +" "+yearToWord(s3));
				}
			}
			else{
				word.append(s1+" "+place(s2)+", "+yearToWord(s3) );
			}
		}
		//month day or month year
		else if( s2.length() != 0 && isANumber(s2) ){
			if( s2.length() < 4 ){
				word.append(s1+" "+place(s2) );
			}
			else{
				word.append(s1+" "+yearToWord(s2) );
			}
		}
		//just month
		else{
			word.append( s1 );
		}
		return word.toString();
	}
	/*
	find out if a string is entirely made of numbers
	*/
	public static boolean isANumber( String s ){
		char [] nums = {'0','1','2','3','4','5','6','7','8','9'};
		boolean test = false;
		for( int i = 0; i < s.length() ; i++ ){
			for( int j = 0; j < nums.length ; j++ ){
				if( s.charAt(i) == nums[j] ){
					test = true;
				}
			}
			if( test == false){ return false; }
			test = false;
		}
		return true;
	}
	/*
	find out if a string is a month
	*/
	public static boolean month( String s ){
		ArrayList<String> months = new ArrayList<String>();
		months.add("january");
		months.add("february");
		months.add("march");
		months.add("april");
		months.add("may");
		months.add("june");
		months.add("july");
		months.add("august");
		months.add("september");
		months.add("october");
		months.add("november");
		months.add("december");
		months.add("jan");
			months.add("feb");
			months.add("mar");
			months.add("apr");
			months.add("may");
			months.add("jun");
			months.add("jul");
			months.add("aug");
			months.add("sept");
			months.add("sep");
			months.add("oct");
			months.add("nov");
			months.add("dec");
			
			months.add("jan.");
			months.add("feb.");
			months.add("mar.");
			months.add("apr.");
			months.add("may.");
			months.add("jun.");
			months.add("jul.");
			months.add("aug.");
			months.add("sept.");
			months.add("sep.");
			months.add("oct.");
			months.add("nov.");
			months.add("dec.");
			
		s = s.toLowerCase();
		if( months.contains(s) ){ return true; }
		else{ return false; }
	}
	/* 
	see if a string contains at least one number 
	*/
	public static boolean containsNum( String s ){
		char [] nums = {'0','1','2','3','4','5','6','7','8','9'};
		for( int i = 0; i < s.length() ; i++ ){
			for( int j = 0; j < nums.length ; j++ ){
				if( s.charAt(i) == nums[j] ){
					return true;
				}
			}
		}
		return false;
	}
	/* 
	converts a string of a number from the text file into words
	*/
	public static String toWord( String s ){
		//get rid of periods attached to numbers
		if( s.charAt(s.length()-1) == '.' ){
			s = s.substring( 0, s.length()-1);
		}
		
		//For 42nd and 1st ect.
		if( s.length() >2 ){
			String end = s.substring( s.length()-2, s.length() );
			if( end.equals("nd")||end.equals("st")||end.equals("rd")||end.equals("th") ){
				return( place( s.substring( 0, s.length()-2 ) ) );
			}
		}
		
		char [] ar = s.toCharArray();
		s = "";
		boolean decimal = false;
		boolean fraction = false;
		boolean dollar = false;
		int pos = -1;
		//get rid of "," and "\"
		for( int i = 0 ; i < ar.length ; i++ ){
			if( ar[i] != ',' && ar[i] != '\\'){
				s = s+ar[i];
			}
		}
		ar = s.toCharArray();
		//find out if the number is a fraction or decimal
		for( int i = 0 ; i < ar.length ; i++ ){
			if( ar[i] == '.' ){ decimal = true; pos = i;}
			if( ar[i] == '/' ){ fraction = true; pos = i; }
		}
		if( !decimal && !fraction ){
			return numConvert(s);
		}
		if( decimal ){
			return (numConvert(s.substring(0,pos))+"point "+decimalEnd(s.substring(pos+1,s.length())));
		}
		if( fraction ){
			boolean plural = false;
			if( Integer.parseInt(s.substring(0,pos)) > 1 ){
				plural = true;
			}
			return ( numConvert(s.substring(0,pos))+" "+fraction(s.substring(pos+1,s.length()),plural)+" ");
		}
		
		
		return "";
	}
	/*
	convert a number, place or day into a string
	examples: 2nd=second january 1 to january first
	*/
	public static String place( String s ){
		HashMap<Integer,String> map = new HashMap<Integer,String>();
		if(true){
        map.put(0,"zeroth");
        map.put(1,"first");
        map.put(2,"second");
        map.put(3,"third");
        map.put(4,"fourth");
        map.put(5,"fifth");
        map.put(6,"sixth");
        map.put(7,"seventh");
        map.put(8,"eighth");
        map.put(9,"nineth");
        map.put(10,"tenth");
        map.put(11,"eleventh");
        map.put(12,"twelfth");
        map.put(13,"thirteenth");
        map.put(14,"fourteenth");
        map.put(15,"fifteenth");
        map.put(16,"sixteenth");
		map.put(17,"seventeenth");
        map.put(18,"eighteenth");
        map.put(19,"nineteenth");
        map.put(20,"twentieth");
        map.put(30,"thirtieth");
        map.put(40,"fortieth");
        map.put(50,"fiftieth");
        map.put(60,"sixtieth");
        map.put(70,"seventieth");
        map.put(80,"eightieth");
        map.put(90,"ninetieth");
		map.put(100,"hundredth");
		map.put(1000,"thousanth");
		map.put(1000000,"millionth");}
		
		HashMap<Integer,String> map1 = new HashMap<Integer,String>();
		if(true){
		map1.put(20,"Twenty");
        map1.put(30,"Thirty");
        map1.put(40,"Forty");
        map1.put(50,"Fifty");
        map1.put(60,"Sixty");
        map1.put(70,"Seventy");
        map1.put(80,"Eighty");
        map1.put(90,"Ninety");
		}
		
		//For 42nd and 1st ect.
		if( s.length() >2 ){
			String end = s.substring( s.length()-2, s.length() );
			if( end.equals("nd")||end.equals("st")||end.equals("rd")||end.equals("th") ){
				return( place( s.substring( 0, s.length()-2 ) ) );
			}
		}
		char [] ar = s.toCharArray();
		s = "";
		//remove commas
		for( int i = 0 ; i < ar.length ; i++ ){
			if( ar[i] != ',' ){
				s = s+ar[i];
			}
		}
		if( s.length() == 0 ){
			return "";
		}
		//if the number is in the map already return it
		if( map.containsKey(Integer.parseInt(s))){
			return map.get(Integer.parseInt(s));
		}
		//else make it
		if( s.length() == 2 ){
			Integer tInt = Integer.parseInt( s.charAt(0) +"") * 10;
			Integer tInt1 = Integer.parseInt( s.charAt(1) +"");
			return ( map1.get(tInt)+ " " + map.get( tInt1 ) );
		}
		return "";
		
	}
	/*
	this method converts the denominator of a fraction into words
	*/
	public static String fraction( String s, boolean plural){
		HashMap<Integer,String> map = new HashMap<Integer,String>();
		if(true){
        map.put(0,"zeroth");
        map.put(1,"over one");
        map.put(2,"half");
        map.put(3,"third");
        map.put(4,"fourth");
        map.put(5,"fifth");
        map.put(6,"sixth");
        map.put(7,"seventh");
        map.put(8,"eighth");
        map.put(9,"nineth");
        map.put(10,"tenth");
        map.put(11,"eleventh");
        map.put(12,"twelfth");
        map.put(13,"thirteenth");
        map.put(14,"fourteenth");
        map.put(15,"fifteenth");
        map.put(16,"sixteenth");
		map.put(17,"seventeenth");
        map.put(18,"eighteenth");
        map.put(19,"nineteenth");
        map.put(20,"twentieth");
        map.put(30,"thirtieth");
        map.put(40,"fortieth");
        map.put(50,"fiftieth");
        map.put(60,"sixtieth");
        map.put(70,"seventieth");
        map.put(80,"eightieth");
        map.put(90,"ninetieth");
		map.put(100,"hundredth");
		map.put(1000,"thousanth");
		map.put(1000000,"millionth");}
		
		HashMap<Integer,String> map1 = new HashMap<Integer,String>();
		if(true){
		map1.put(20,"Twenty");
        map1.put(30,"Thirty");
        map1.put(40,"Forty");
        map1.put(50,"Fifty");
        map1.put(60,"Sixty");
        map1.put(70,"Seventy");
        map1.put(80,"Eighty");
        map1.put(90,"Ninety");
		}
		
		//For 42nd and 1st ect.
		if( s.length() >2 ){
			String end = s.substring( s.length()-2, s.length() );
			if( end.equals("nd")||end.equals("st")||end.equals("rd")||end.equals("th") ){
				return( place( s.substring( 0, s.length()-2 ) ) );
			}
		}
		char [] ar = s.toCharArray();
		s = "";
		for( int i = 0 ; i < ar.length ; i++ ){
			if( ar[i] != ',' ){
				s = s+ar[i];
			}
		}
		if( s.length() == 0 ){
			return "";
		}
		if( map.containsKey(Integer.parseInt(s))){
			if( plural ){
				return (map.get(Integer.parseInt(s))+"s");
			}
			else{ return map.get(Integer.parseInt(s)); }
		}
		if( s.length() == 2 ){
			Integer tInt = Integer.parseInt( s.charAt(0) +"") * 10;
			Integer tInt1 = Integer.parseInt( s.charAt(1) +"");
			if( plural ){
				return ( map1.get(tInt)+ " " + map.get( tInt1 )+"s" );
			}
			else { return ( map1.get(tInt)+ " " + map.get( tInt1 ) ); }
		}
		return "";
		
	}
	/*
	this makes the part of a decimal number after the "."
	into words 
	*/
	public static String decimalEnd( String s ){
		HashMap<Integer,String> map = new HashMap<Integer,String>();
        map.put(0,"zero");
        map.put(1,"one");
        map.put(2,"two");
        map.put(3,"three");
        map.put(4,"four");
        map.put(5,"five");
        map.put(6,"six");
        map.put(7,"seven");
        map.put(8,"eight");
        map.put(9,"nine");
		ArrayList<String> nums = new ArrayList<String>();
		nums.add("1"); nums.add("2");nums.add("3");nums.add("4");nums.add("5");nums.add("6");
		nums.add("7");nums.add("8");nums.add("9");nums.add("0");
		
		String temp = "";
		//get rid of non number characters
		for( int j = 0; j < s.length() ; j++ ){
			if( nums.contains(s.charAt(j)+"") == true ){
				temp = temp + s.charAt(j);
			}
		}
		s = temp;
		temp = "";
		
		StringBuilder word = new StringBuilder("");
		for( int i = 0; i < s.length() ; i++ ){
			word.append( map.get( Integer.parseInt( s.charAt(i)+"")) +" ");
		}
		return word.toString();
	}
	/*
	this method takes a number and begins converting it into words
	it is called by toWord() and calls numToWord()
	*/
	public static String numConvert( String s ){
		
		HashMap<Integer,String> map = new HashMap<Integer,String>();
        map.put(0,"zero");
        map.put(1,"one");
        map.put(2,"two");
        map.put(3,"three");
        map.put(4,"four");
        map.put(5,"five");
        map.put(6,"six");
        map.put(7,"seven");
        map.put(8,"eight");
        map.put(9,"nine");
        map.put(10,"ten");
        map.put(11,"eleven");
        map.put(12,"twelve");
        map.put(13,"thirteen");
        map.put(14,"fourteen");
        map.put(15,"fifteen");
        map.put(16,"sixteen");
		map.put(17,"seventeen");
        map.put(18,"eighteen");
        map.put(19,"nineteen");
        map.put(20,"twenty");
        map.put(30,"thirty");
        map.put(40,"forty");
        map.put(50,"fifty");
        map.put(60,"sixty");
        map.put(70,"seventy");
        map.put(80,"eighty");
        map.put(90,"ninety");
         
		ArrayList<String> nums = new ArrayList<String>();
		nums.add("1"); nums.add("2");nums.add("3");nums.add("4");nums.add("5");nums.add("6");
		nums.add("7");nums.add("8");nums.add("9");nums.add("0");
		
		StringBuilder word = new StringBuilder();
		String temp = "";
		boolean firstWord = true;
		for( int j = 0; j < s.length() ; j++ ){
			if( nums.contains(s.charAt(j)+"") == true ){
				temp = temp + s.charAt(j);
			}
		}
		s = temp;
		temp = "";
		//for trillions
		if( s.length() - 12 > 0 ){
			for( int i = 0; i < s.length()-12; i++){
				temp = temp + s.charAt(i);
			}
			word.append( numToWord(temp, map, firstWord)+"trillion ");
			s = s.substring( s.length()-12, s.length() );
			temp = "";
			firstWord = false;
		}
		//for billions
		if( s.length() - 9 > 0 ){
			for( int i = 0; i < s.length()-9; i++){
				temp = temp + s.charAt(i);
			}
			word.append( numToWord(temp, map, firstWord)+"billion ");
			s = s.substring( s.length()-9, s.length() );
			temp = "";
			firstWord = false;
		}
		//for millions
		if( s.length() - 6 > 0 ){
			for( int i = 0; i < s.length()-6; i++){
				temp = temp + s.charAt(i);
			}
			word.append( numToWord(temp, map, firstWord)+"million ");
			s = s.substring( s.length()-6, s.length() );
			temp = "";
			firstWord = false;
		}
		//for thousands
		if( s.length() - 3 > 0 ){
			for( int i = 0; i < s.length()-3; i++){
				temp = temp + s.charAt(i);
			}
			word.append( numToWord(temp, map, firstWord)+"thousand ");
			s = s.substring( s.length()-3, s.length() );
			temp = "";
			firstWord = false;
		}
		//for under one thousand
		if( s.length() > 0 ){
			for( int i = 0; i < s.length(); i++){
				temp = temp + s.charAt(i);
			}
			word.append( numToWord(temp, map, firstWord));
			//s = s.substring( 3, s.length() );
			temp = "";
			firstWord = false;
		}
		return word.toString();
	}
	/*
	This is called by numConvert and taks a number between
	0 and 999 then converts it to words.
	*/
	public static String numToWord( String s, HashMap<Integer,String> map, boolean firstWord){
		//s is a number between 0 and 999, inclusive
		String temp = "";
		Integer tempInt = 0;
		StringBuilder word = new StringBuilder("");
		//if( firstWord ){ word.append(" ") ; firstWord = false; }
		for( int i = 0; i < s.length()  ; i++ ){
			temp = temp + s.charAt(i);
		}
		Integer num = Integer.parseInt( temp );
		//if the number has a hundreds digit number
		if( temp.length() == 3 && temp.charAt(0) != '0'){
			tempInt = Integer.parseInt(temp.charAt(0)+"");
			word.append(  map.get(tempInt) + " hundred " );
			temp = temp.charAt(1) + ""+temp.charAt(2);
		}
		if( temp.charAt(0) == '0' && temp.length() == 3){
			temp = temp.charAt(1) + ""+temp.charAt(2);
		}
		if( temp.length() == 2 && temp.charAt(0)!='0'){
			tempInt = Integer.parseInt(temp);
			if( ! map.containsKey( tempInt ) ){
				tempInt = Integer.parseInt(temp.charAt(0)+"");
				Integer tempInt1 = Integer.parseInt(temp.charAt(1)+"");
				word.append(  map.get(tempInt*10) +" "+ map.get(tempInt1 ) + " ");
				temp =  "";
			}
			else{
				word.append( map.get(tempInt) + " ");			
				temp = "";
			}
		}
		if( temp.length() >1 ){
			if( temp.charAt(0) == '0'){
				temp = temp.charAt(1) + "";
			}
		}
		if( temp.length() == 1 && temp.equals("0") && firstWord ){
			word.append( map.get( Integer.parseInt(temp) ) +" ");
		}
		else if( temp.length() == 1 && temp.equals("0") == false ){
			word.append( map.get( Integer.parseInt(temp) ) +" ");
		}
		return word.toString();
	}
}











