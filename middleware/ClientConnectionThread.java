import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnectionThread implements Runnable {

	private Socket clientSocket = null;

	public ClientConnectionThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public void run() {
		PrintWriter clientOutput;
		BufferedReader clientInput;
		try {
			// Prints output at the client
			clientOutput = new PrintWriter(
					clientSocket.getOutputStream(), true);
			// Reads input from the client
			clientInput = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));
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
				clientOutput.write("received input: " + inputLine);
			} catch (IOException e) {
				// Couldn't read from client input
				e.printStackTrace();
				return;
			}
		}
		

	}
}
