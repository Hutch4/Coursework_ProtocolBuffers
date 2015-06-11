package uk.ac.st_andrews.cs.cs4301.worker;

import java.util.ArrayList;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;

import com.google.protobuf.InvalidProtocolBufferException;

import uk.ac.st_andrews.cs.cs4301.common.CalculationSet;
import uk.ac.st_andrews.cs.cs4301.common.WireMessages.ComputationMessage;

public class WorkReceiver implements Runnable{

	private Context context;
	private String master;
	
	public WorkReceiver(Context context, String master){
		this.context = context;
		this.master = master;
	}
	
	public void run() {
		// Socket to receive messages on
		ZMQ.Socket receiver = context.socket(ZMQ.PULL);
		receiver.connect("tcp://"+master+":5557");
		
		//TODO: parse computation message
		try {
			//we receive the message but it has no properties to decode because
			//we haven't implemented ComputationMessage yet.
			ComputationMessage work = ComputationMessage.parseFrom(receiver.recv());
			System.out.println(work);
			
			CalculationSet wcs = new CalculationSet((ArrayList<Integer>) work.getValuesList());
			
			//int result = wcs.calculateProduct();
			int result = executeCalculations(work);
			ResultSender rs = new ResultSender(context, master);
			rs.sendToSink(result, work.getId());
			
			//once you have decoded the message you probably want to call
			//a method from a another class to perform the computation
		} catch (InvalidProtocolBufferException e) {
			System.err.println("Unable to decode work message");
			e.printStackTrace();
		}
	}
	
	public int executeCalculations(ComputationMessage work){
		CalculationSet wcs = new CalculationSet((ArrayList<Integer>) work.getValuesList());
		int result=0;
		if(work.getType().getType() == ComputationMessage.CalID.Product){
			result = wcs.calculateProduct();
			return result;
		}
		if(work.getType().getType() == ComputationMessage.CalID.Max){
			result = wcs.calculateMax();
			return result;
		}
		if(work.getType().getType() == ComputationMessage.CalID.Min){
			result = wcs.calculateMin();
			return result;
		}
		return result;
	}

}
