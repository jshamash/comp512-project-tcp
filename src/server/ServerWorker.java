package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

import messages.Command;
import messages.ReplyMessage;
import messages.RequestMessage;
import ResInterface.ResourceManager;

public class ServerWorker extends Thread {

	private Socket clientSocket;
	private ResourceManager rm;

	public ServerWorker(Socket clientSocket, ResourceManager rm) {
		this.clientSocket = clientSocket;
		this.rm = rm;
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
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return;
	}

	private ReplyMessage process(RequestMessage input) {
		Object[] params = input.getParams();
		Command cmd = input.getCommand();
		ReplyMessage replyMessage = null;
		Object reply;

		synchronized (rm) {
			switch (cmd) {
			case ADD_FLIGHT:
				reply = rm.addFlight((Integer) params[0], (Integer) params[1],
						(Integer) params[2], (Integer) params[3]);
				replyMessage = new ReplyMessage(cmd, reply);
				break;
			case ADD_CARS:
				reply = rm.addCars((Integer) params[0], (String) params[1],
						(Integer) params[2], (Integer) params[3]);
				replyMessage = new ReplyMessage(cmd, reply);
				break;
			case ADD_ROOMS:
				reply = rm.addRooms((Integer) params[0], (String) params[1],
						(Integer) params[2], (Integer) params[3]);
				replyMessage = new ReplyMessage(cmd, reply);
				break;
			case DELETE_FLIGHT:
				reply = rm.deleteFlight((Integer) params[0],
						(Integer) params[1]);
				replyMessage = new ReplyMessage(cmd, reply);
				break;
			case DELETE_CARS:
				reply = rm.deleteCars((Integer) params[0], (String) params[1]);
				replyMessage = new ReplyMessage(cmd, reply);
				break;
			case DELETE_ROOMS:
				reply = rm.deleteRooms((Integer) params[0], (String) params[1]);
				replyMessage = new ReplyMessage(cmd, reply);
				break;
			case QUERY_FLIGHT:
				reply = rm
						.queryFlight((Integer) params[0], (Integer) params[1]);
				replyMessage = new ReplyMessage(cmd, reply);
				break;
			case QUERY_CARS:
				reply = rm.queryCars((Integer) params[0], (String) params[1]);
				replyMessage = new ReplyMessage(cmd, reply);
				break;
			case QUERY_ROOMS:
				reply = rm.queryRooms((Integer) params[0], (String) params[1]);
				replyMessage = new ReplyMessage(cmd, reply);
				break;
			case QUERY_CARS_PRICE:
				reply = rm.queryCarsPrice((Integer) params[0],
						(String) params[1]);
				replyMessage = new ReplyMessage(cmd, reply);
				break;
			case QUERY_FLIGHT_PRICE:
				reply = rm.queryFlightPrice((Integer) params[0],
						(Integer) params[1]);
				replyMessage = new ReplyMessage(cmd, reply);
				break;
			case QUERY_ROOMS_PRICE:
				reply = rm.queryRoomsPrice((Integer) params[0],
						(String) params[1]);
				replyMessage = new ReplyMessage(cmd, reply);
				break;
			case RESERVE_FLIGHT:
				reply = rm.reserveFlight((Integer) params[0],
						(Integer) params[1], (Integer) params[2]);
				replyMessage = new ReplyMessage(cmd, reply);
				break;
			case RESERVE_CAR:
				reply = rm.reserveCar((Integer) params[0], (Integer) params[1],
						(String) params[2]);
				replyMessage = new ReplyMessage(cmd, reply);
				break;
			case RESERVE_ROOM:
				reply = rm.reserveRoom((Integer) params[0],
						(Integer) params[1], (String) params[2]);
				replyMessage = new ReplyMessage(cmd, reply);
				break;
			case RESERVE_ITINERARY:
				reply = rm.reserveItinerary((Integer) params[0],
						(Integer) params[1], (Vector) params[2],
						(String) params[3], (Boolean) params[4],
						(Boolean) params[5]);
				replyMessage = new ReplyMessage(cmd, reply);
				break;
			case REMOVE_RESERVATIONS:
				reply = rm.removeReservations((Integer) params[0],
						(String) params[1], (Integer) params[2]);
				replyMessage = new ReplyMessage(cmd, reply);
				break;
			default:
				System.err.println("Unexpected command type: " + cmd);
			}
		}

		return replyMessage;
	}

}
