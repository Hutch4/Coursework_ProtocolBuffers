package uk.ac.st_andrews.cs.cs4301.master;

import java.io.IOException;
import java.util.ArrayList;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;

import uk.ac.st_andrews.cs.cs4301.common.WireMessages.ComputationMessage;
import uk.ac.st_andrews.cs.cs4301.common.WireMessages.ComputationMessage.CalType;

public class Rescheduler implements Runnable{
	private Context context;
	private ArrayList<Computation> workParts;
	int calc_id = 0;
	
	public Rescheduler(ZMQ.Context context, ArrayList<Computation> workParts, int calc_id){
		this.context = context;
		this.workParts = workParts;
		this.calc_id = calc_id;
	}

	public void reschedule(int id){
		ZMQ.Socket sender = context.socket(ZMQ.PUSH);
		sender.bind("tcp://*:5557");
		ComputationMessage.Builder message = ComputationMessage.newBuilder();
		message.addAllValues(workParts.get(id).getAllValues());
		message.setId(id);
		ComputationMessage.CalType.Builder type = CalType.newBuilder();
		type.setType(ComputationMessage.CalID.Product);
		message.setType(setType(type).build());
		for(int i = 0; i < 2; i ++){
			sender.send(message.build().toByteArray());
		}
	}

	public void run() {
		// TODO Auto-generated method stub
	}
	
	public ComputationMessage.CalType.Builder setType(ComputationMessage.CalType.Builder type){
		if(calc_id == 0){
			type.setType(ComputationMessage.CalID.Product);
			return type;
		}
		if(calc_id == 1){
			type.setType(ComputationMessage.CalID.Max);
			return type;
		}
		if(calc_id == 2){
			type.setType(ComputationMessage.CalID.Min);
			return type;
		}
		return type;
	}
}

