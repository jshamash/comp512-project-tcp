package server;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import messages.ReplyMessage;
import messages.RequestMessage;


public class ServerWorker extends Thread {

	private Socket clientSocket;
	
	public ServerWorker(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {
		ObjectInputStream in;
		ObjectOutputStream out;
		try {
			in = new ObjectInputStream(clientSocket.getInputStream());
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			
			// We expect one line from the client.
			RequestMessage input = (RequestMessage) in.readObject();
			ReplyMessage output = process(input);
			System.out.println("Sending \"" + output + "\" to outstream");
			out.writeObject(output);
			
			// My job here is done!
			out.close();
			in.close();
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return;
		
	}

	private ReplyMessage process(RequestMessage input) {
		// TODO Auto-generated method stub
		return new ReplyMessage(input.getCommand(), true);
	}

}
