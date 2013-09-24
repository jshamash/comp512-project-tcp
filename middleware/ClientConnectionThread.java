import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * One of these threads gets run per client. We don't need to worry about
 * concurrent requests from client, since client blocks waiting for response.
 * 
 * @author Jake Shamash
 *  
 */
public class ClientConnectionThread implements Runnable {

	private Socket clientSocket = null;
	private String host1;
	private int port1;

	public ClientConnectionThread(Socket clientSocket, String host1, int port1) {
		this.clientSocket = clientSocket;
		this.host1 = host1;
		this.port1 = port1;
	}

	public void run() {
		PrintWriter clientOutput;
		BufferedReader clientInput;
		try {
			// Prints output at the client
			clientOutput = new PrintWriter(clientSocket.getOutputStream());
			// Reads input from the client
			clientInput = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		// Handle incoming requests from client
		String inputLine;
		while (true) {
			try {
				System.out.println("Waiting for client input...");
				inputLine = clientInput.readLine();
				if (inputLine == null) {
					System.out.println("Closing connection with a client...");
					clientSocket.close();
					return;
				}
				System.out.println("Got client input: " + inputLine);
				// Here we will look at the input to see what needs to be done
				// with it.

				// clientOutput.println("received input: " + inputLine);
			} catch (IOException e) {
				// Couldn't read from client input
				e.printStackTrace();
				return;
			}
		}

	}
}
