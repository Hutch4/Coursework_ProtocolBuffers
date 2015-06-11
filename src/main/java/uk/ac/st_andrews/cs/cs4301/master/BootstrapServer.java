package uk.ac.st_andrews.cs.cs4301.master;
import org.zeromq.ZMQ;
import com.google.protobuf.InvalidProtocolBufferException;
import uk.ac.st_andrews.cs.cs4301.common.WireMessages.BootstrapMessage;
import uk.ac.st_andrews.cs.cs4301.common.WireMessages.ResponseMessage;

public class BootstrapServer implements Runnable{
	private ZMQ.Socket responder;
	private boolean running;

	public BootstrapServer(ZMQ.Context context){
		this.responder = context.socket(ZMQ.REP);
		this.running = true;
	}

	public void run(){
			responder.bind("tcp://*:5555");

			while(running){

				byte[] encodedReponse = null;
				ResponseMessage.Builder response = ResponseMessage.newBuilder();
				
				//receive profobuf encoded message
				byte[] message = responder.recv(0);
				
				try {
					BootstrapMessage parsedMessage = BootstrapMessage.parseFrom(message);
					String worker = parsedMessage.getAddress();
					System.out.println("Bootstrapped " + worker);
					
					
					//we're using http style response codes, 200 OK
					response.setStatus(200);
					//build reply to byte array to send
					encodedReponse = response.build().toByteArray();
				} catch (InvalidProtocolBufferException e) {
					System.err.println("Unable to parse bootstrap message");
					//we're using http style response codes, 400 failure
					response.setStatus(400);
					//build reply to byte array to send
					encodedReponse = response.build().toByteArray();
					e.printStackTrace();
				} finally{
					responder.send(encodedReponse);

				}	
			}

	}

	public void stop(){
		this.running = false;
		this.responder.close();
	}

	public static void main(String[] args){
 		ZMQ.Context context = ZMQ.context(1);
		Thread t = new Thread(new BootstrapServer(context));
		t.start();
	}

}