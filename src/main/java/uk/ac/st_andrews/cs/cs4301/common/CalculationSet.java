package uk.ac.st_andrews.cs.cs4301.common;

import java.awt.List;
import java.util.ArrayList;
import java.util.Iterator;

public class CalculationSet {

	ArrayList<Integer> values = new ArrayList<Integer>();
	
	public CalculationSet(){
		
	}
	
	public CalculationSet(ArrayList<Integer> values){
		this.values = values;
	}
	
	public void addValues(ArrayList<Integer> values){
		this.values.addAll(values);
	}
	
	/*
	 * Calculation Methods
	 */
	
	public int calculateProduct(){
		Iterator<Integer> iter = values.listIterator();
		
		int result = 0;
		while(iter.hasNext()){
			if(result == 0){
				result = iter.next();
			}
			while(iter.hasNext()){
				result = result * iter.next();
			}
			break;
		}
		return result;
	}
	
	public int calculateMax(){
		Iterator<Integer> iter = values.listIterator();
		
		int result = 0;
		while(iter.hasNext()){
			if(result == 0){
				result = iter.next();
			}
			while(iter.hasNext()){
				int temp = iter.next();
				if(result < temp){
					result = temp;
				}
			}
			break;
		}
		return result;
	}
	
	public int calculateMin(){
		Iterator<Integer> iter = values.listIterator();
		
		int result = 0;
		while(iter.hasNext()){
			if(result == 0){
				result = iter.next();
			}
			while(iter.hasNext()){
				int temp = iter.next();
				if(result > temp){
					result = temp;
				}
			}
			break;
		}
		return result;
	}
}
