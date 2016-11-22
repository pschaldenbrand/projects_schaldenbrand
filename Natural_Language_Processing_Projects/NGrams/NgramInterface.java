import java.io.*;
import java.util.*;

public interface NgramInterface {
	
	public void make( String s ) throws IOException;
		//Create the Ngram
	public void view();
		//Print the gram to the screen
	public String perplexity( String line );
		//find the perplexity of a String
	public void develop( String fn )throws IOException;
		//dev for the Sherlock Holmes problem
	public void develop1( String fn) throws IOException;
		//dev for everything else
}