package uk.ac.st_andrews.cs.cs4301.worker;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;

import uk.ac.st_andrews.cs.cs4301.common.WireMessages.ResultMessage;

public class ResultSender {
	private Context context;
	private String master;
	
	public ResultSender(Context context, String master){
		this.context = context;
		this.master = master;
	}
	
	public void sendToSink(int result, int id){
		ZMQ.Socket sender = context.socket(ZMQ.PUSH);
		sender.connect("tcp://"+master+":5558");
		
		ResultMessage.Builder message = ResultMessage.newBuilder();
		message.setResult(result);
		message.setId(id);
		sender.send(message.build().toByteArray());
		sender.close();
	}
}
