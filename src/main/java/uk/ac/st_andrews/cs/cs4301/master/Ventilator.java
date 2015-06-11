package uk.ac.st_andrews.cs.cs4301.master;

import java.io.IOException;
import java.util.ArrayList;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;

import uk.ac.st_andrews.cs.cs4301.common.WireMessages.ComputationMessage;
import uk.ac.st_andrews.cs.cs4301.common.WireMessages.ComputationMessage.CalType;

public class Ventilator implements Runnable{
	private Context context;
	private ArrayList<Computation> workParts;
	int calculation_id;
	public Ventilator(ZMQ.Context context, ArrayList<Computation> workParts, int calculation_id){
		this.context = context;
		this.workParts = workParts;
		this.calculation_id = calculation_id;
	}

	
	/**
	 * This thread distributes work amongst the connected workers
	 * you'll need to implement the Computation class or replace
	 * it with something else and complete the ComputationMessage
	 * and the builder to make this method actually send out work
	 */
	public void run() {
		ZMQ.Socket sender = context.socket(ZMQ.PUSH);
		sender.bind("tcp://*:5557");
		
		//this lets you go to another machine to start a worker
		System.out.println("Press Enter when the workers are ready: ");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Sending tasks to workers\n");
		
		int id = 0;
		for (Computation work : workParts){
			for(int i = 0; i < 2; i ++){
				//TODO Implement the message format to send work to the workers
				//TODO complete this loop
				ComputationMessage.Builder message = ComputationMessage.newBuilder();
				message.addAllValues(work.getAllValues());
				message.setId(id);
				ComputationMessage.CalType.Builder type = CalType.newBuilder();
				type.setType(ComputationMessage.CalID.Product);
				message.setType(setType(type).build());
				//currently this message type has no properties
				//so we just build it and send an empty message
				sender.send(message.build().toByteArray());
			}
			id ++;
		}
	}
	
	public ComputationMessage.CalType.Builder setType(ComputationMessage.CalType.Builder type){
		if(calculation_id == 0){
			type.setType(ComputationMessage.CalID.Product);
			return type;
		}
		if(calculation_id == 1){
			type.setType(ComputationMessage.CalID.Max);
			return type;
		}
		if(calculation_id == 2){
			type.setType(ComputationMessage.CalID.Min);
			return type;
		}
		return type;
	}
}

