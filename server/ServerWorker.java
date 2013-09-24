import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ServerWorker extends Thread {

	private Socket clientSocket;
	
	public ServerWorker(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {
		BufferedReader in;
		PrintWriter out;
		try {
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			
			// We expect one line from the client.
			String input = in.readLine();
			String output = process(input);
			System.out.println("Sending \"" + output + "\" to outstream");
			out.println(output);
			
			// My job here is done!
			out.close();
			in.close();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return;
		
	}

	private String process(String input) {
		// TODO Auto-generated method stub
		return "I have processed " + input;
	}

}
