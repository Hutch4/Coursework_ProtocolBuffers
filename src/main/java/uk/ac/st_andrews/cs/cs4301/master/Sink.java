package uk.ac.st_andrews.cs.cs4301.master;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;

import com.google.protobuf.InvalidProtocolBufferException;

import uk.ac.st_andrews.cs.cs4301.common.CalculationSet;
import uk.ac.st_andrews.cs.cs4301.common.WireMessages.ComputationMessage;
import uk.ac.st_andrews.cs.cs4301.common.WireMessages.ResultMessage;

public class Sink implements Runnable {
	private Context context;
	private ArrayList<Integer> results;
	private Rescheduler rs;
	//Stores all the returned results, for each work set id
	private HashMap<Integer, ArrayList<Integer>> map = new HashMap<Integer, ArrayList<Integer>>();
	//used to track the amount of reschedules for each worktask: structured ID, Reschedule Count.
	private HashMap<Integer, Integer> flag = new HashMap<Integer, Integer>();
	
	private int total = 0;
	private int total_tasks = 0;
	private int task_completion_count =0;
	
	public Sink(Context context, ArrayList<Integer> results, Rescheduler rs, int total_tasks) {
		this.results = results;
		this.context = context;
		this.rs = rs;
		this.total_tasks = total_tasks;
	}

	public void run() {
		ZMQ.Socket receiver = context.socket(ZMQ.PULL);
		receiver.bind("tcp://*:5558");

		try {
			ResultMessage message = ResultMessage.parseFrom(receiver.recv());
			int result = message.getResult();
			System.out.println("Received result " + result);
			results.add(result);

			if (map.get(message.getId()) == null) {
				map.put(message.getId(), new ArrayList<Integer>());
				flag.put(message.getId(), 0);
				map.get(message.getId()).add(result);
			}
			
			if (checkResults(message.getId())) {
				total = total * result;
				CalculationSet cs = new CalculationSet(results);
				System.out
						.println("Continuing Total: " + cs.calculateProduct());
				task_completion_count ++;
				if(task_completion_count == total_tasks){
					System.out.println("Final Result is: " + total);
				}
				
			} else {
				// deal with wrong results - reschedule the task to workers. 
				if(flag.get(message.getId()) > 3){
					//Task has failed too many times, give up rescheduling.
					//Some sort of logging system would be useful here.
				}else{
					rs.reschedule(message.getId());
					flag.put(message.getId(), flag.get(message.getId()) + 1);
				}
			}
			
			
		} catch (InvalidProtocolBufferException e) {
			System.err.println("Unable to decode result message");
			e.printStackTrace();
		}

	}

	/*
	 * compares the current results of the same id against each other, returning false if they are not the same.
	 * If only one result is currently held, false is returned also.
	 */
	public boolean checkResults(int id) {
		if (map.get(id) == null) {
			return true;
		}else{
			if(map.get(id).toArray().length <= 1){
				return false;
			}else{
				int length = map.get(id).toArray().length;
				int[] values = new int[length];
				Iterator<Integer> iter = map.get(id).listIterator();
				int count = 0;
				while(iter.hasNext()){
					values[count] = iter.next();
					count++;
				}
				
				for(int i = 0; i < values.length; i ++){
					for(int j = 0; j < values.length; j ++){
						if(values[i] != values[j]){
							return false;
						}
					}
				}
			}
		}
		return true;
	}
}
