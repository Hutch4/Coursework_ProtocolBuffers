package uk.ac.st_andrews.cs.cs4301.master;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;

public class Master {
	public static void main(String[] args){
		//the context is an object shared between all zmq sockets
		Context context = ZMQ.context(1);
		ArrayList<Integer> results = new ArrayList<Integer>();
		
		//create an array list of values to send to workers
		//from the input file
		//you need to populate this arraylist 
		//String dataFile = arg[0];
		ArrayList<Computation> workParts = new ArrayList<Computation>();
		ArrayList<Integer> values = new ArrayList<Integer>();
		FileReader filereader;
		try {
			filereader = new FileReader("toread.txt");
				 BufferedReader bufferedreader = new BufferedReader(filereader);
			     String line = bufferedreader.readLine();
			     //While we have read in a valid line
			     while (line != null) {
			        	String[] s = line.split(" ");
			        	for(int i = 0; i < s.length; i ++){
			        		values.add(Integer.parseInt(s[i]));
			        	}
			     }     
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int total_values = values.toArray().length;
		
		int num_workers = 2;
		
		for(int i = 0; i < num_workers; i ++){
			workParts.add(new Computation());
		}
		
		for(int i = 0; i < total_values; i ++){
			workParts.get(total_values % num_workers).addValue(i);
		}
		
		Thread bootstrapServer = new Thread(new BootstrapServer(context));
		bootstrapServer.start();
		
		Scanner reader = new Scanner(System.in);
		System.out.println("Enter the calcuation number you wish: \n Product: 0 \n Max: 1 \n Min: 2");
		int a=reader.nextInt();
		int calc_id = a;
		//this will distribute work from the workParts arraylist between workers
		//currently workParts is empty, fix this
		Thread ventilator = new Thread(new Ventilator(context, workParts, calc_id));
		ventilator.start();
		
		
		//create the sink to receive results
		Thread sink = new Thread(new Sink(context, results, new Rescheduler(context, workParts, calc_id), num_workers));
		sink.start();	
		
		
	}
}
