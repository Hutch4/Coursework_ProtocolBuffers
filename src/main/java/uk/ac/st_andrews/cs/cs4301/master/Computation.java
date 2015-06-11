package uk.ac.st_andrews.cs.cs4301.master;

import java.util.ArrayList;
import java.util.Iterator;

/*
 * this is a class to store ranges of values to
 * be distributed between workers
 */
public class Computation {

	ArrayList<Integer> values = new ArrayList<Integer>();
	
	public Computation(){
	}
	
	public void addValues(ArrayList<Integer> values){
		this.values.addAll(values);
	}
	
	public void addValue(int value){
		this.values.add(value);
	}
	
	public ArrayList<Integer> getAllValues(){
		return this.values;
	}
	
	

	
}
