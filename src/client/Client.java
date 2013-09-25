package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.Vector;

import messages.Command;
import messages.ReplyMessage;
import messages.RequestMessage;

public class Client {

	static Socket socket = null;
	static ObjectOutputStream out = null;
	static ObjectInputStream in = null;
	static BufferedReader stdIn = new BufferedReader(new InputStreamReader(
			System.in));

	static String message = "blank";

	/**
	 * Makes a connection to middleware server.
	 * 
	 * @param args
	 *            The host to connect to followed by the port.
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage: java Client [mw-host] [mw-port]");
			exit(-1);
		}

		String host = args[0];
		int port = Integer.parseInt(args[1]);

		try {
			// TODO put this in client constructor
			socket = new Socket(host, port);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			System.err.println("Unknown host: " + host);
			exit(-1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: "
					+ host);
			exit(-1);
		}

		// Read from stdin
		Client client = new Client();
		String userInput;
		String command = "";
		Vector arguments = new Vector();

		System.out.println("\n\n\tClient Interface");
		System.out.println("Type \"help\" for list of supported commands");

		while (true) {
			System.out.print("\n>");
			try {
				// read the next command
				command = stdIn.readLine();
			} catch (IOException io) {
				System.out.println("Unable to read from standard in");
				exit(-1);
			}
			// remove heading and trailing white space
			try {
				command = command.trim();
				arguments = client.parse(command);
				RequestMessage requestMessage = client.getCommand(command, arguments);
				if (requestMessage != null) {
					out.writeObject(requestMessage);
					ReplyMessage received = (ReplyMessage) in.readObject();
					System.out.println();
					printReply(received);
				}
			} catch (Exception e) {
				System.err.println("EXCEPTION:");
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private static void printReply(ReplyMessage received) {
		switch (received.getCommand()) {
		case ADD_CARS:
			if ((Boolean) received.getReturnValue())
				System.out.println("Cars added");
			else
				System.out.println("Cars could not be added");
			break;
		case ADD_FLIGHT:
			if ((Boolean) received.getReturnValue())
				System.out.println("Flight added");
			else
				System.out.println("Flight could not be added");
			break;
		case ADD_ROOMS:
			if ((Boolean) received.getReturnValue())
				System.out.println("Rooms added");
			else
				System.out.println("Rooms could not be added");
			break;
		case DELETE_CARS:
			if ((Boolean) received.getReturnValue())
				System.out.println("Cars Deleted");
			else
				System.out.println("Cars could not be deleted");
			break;
		case DELETE_CUSTOMER:
			if ((Boolean) received.getReturnValue())
				System.out.println("Customer Deleted");
			else
				System.out.println("Customer could not be deleted");
			break;
		case DELETE_FLIGHT:
			if ((Boolean) received.getReturnValue())
				System.out.println("Flight Deleted");
			else
				System.out.println("Flight could not be deleted");
			break;
		case DELETE_ROOMS:
			if ((Boolean) received.getReturnValue())
				System.out.println("Rooms Deleted");
			else
				System.out.println("Rooms could not be deleted");
			break;
		case NEW_CUSTOMER:
			System.out.println("new customer id:" + (Integer) received.getReturnValue());
			break;
		case NEW_CUSTOMER_CID:
			break;
		case QUERY_CARS:
			System.out.println("number of Cars at this location:" + (Integer) received.getReturnValue());
			break;
		case QUERY_CARS_PRICE:
			System.out.println("Price of a car at this location:" + (Integer) received.getReturnValue());
			break;
		case QUERY_CUSTOMER_INFO:
			System.out.println("Customer info:" + (String) received.getReturnValue());
			break;
		case QUERY_FLIGHT:
			System.out.println("Number of seats available:" + (Integer) received.getReturnValue());
			break;
		case QUERY_FLIGHT_PRICE:
			System.out.println("Price of a seat:" + (Integer) received.getReturnValue());
			break;
		case QUERY_ROOMS:
			System.out.println("number of Rooms at this location:" + (Integer) received.getReturnValue());
			break;
		case QUERY_ROOMS_PRICE:
			System.out.println("Price of Rooms at this location:" + (Integer) received.getReturnValue());
			break;
		case RESERVE_CAR:
			if ((Boolean) received.getReturnValue())
				System.out.println("Car Reserved");
			else
				System.out.println("Car could not be reserved.");
			break;
		case RESERVE_FLIGHT:
			if ((Boolean) received.getReturnValue())
				System.out.println("Flight Reserved");
			else
				System.out.println("Flight could not be reserved.");
			break;
		case RESERVE_ITINERARY:
			if ((Boolean) received.getReturnValue())
				System.out.println("Itinerary Reserved");
			else
				System.out.println("Itinerary could not be reserved.");
			break;
		case RESERVE_ROOM:
			if ((Boolean) received.getReturnValue())
				System.out.println("Room Reserved");
			else
				System.out.println("Room could not be reserved.");
			break;
		default:
			break;
		}
		
	}

	/**
	 * Verifies syntax and parses request as a message.
	 * 
	 * @param command
	 * @param arguments
	 * @return
	 * @throws Exception
	 */
	public RequestMessage getCommand(String command, Vector arguments)
			throws Exception {

		int Id, Cid;
		int flightNum;
		int flightPrice;
		int flightSeats;
		boolean room;
		boolean car;
		int price;
		int numRooms;
		int numCars;
		String location;

		// decide which of the commands this was
		switch (findChoice((String) arguments.elementAt(0))) {
		case 1: // help section
			if (arguments.size() == 1) // command was "help"
				listCommands();
			else if (arguments.size() == 2) // command was
											// "help <commandname>"
				listSpecific((String) arguments.elementAt(1));
			else
				// wrong use of help command
				System.out
						.println("Improper use of help command. Type help or help, <commandname>");
			return null;

		case 2: // new flight
			if (arguments.size() != 5) {
				wrongNumber();
				break;
			}
			System.out.println("Adding a new Flight using id: "
					+ arguments.elementAt(1));
			System.out.println("Flight number: " + arguments.elementAt(2));
			System.out.println("Add Flight Seats: " + arguments.elementAt(3));
			System.out.println("Set Flight Price: " + arguments.elementAt(4));

			Id = getInt(arguments.elementAt(1));
			flightNum = getInt(arguments.elementAt(2));
			flightSeats = getInt(arguments.elementAt(3));
			flightPrice = getInt(arguments.elementAt(4));
			return new RequestMessage(Command.ADD_FLIGHT, new Object[] { Id,
					flightNum, flightSeats, flightPrice });

		case 3: // new Car
			if (arguments.size() != 5) {
				wrongNumber();
				break;
			}
			System.out.println("Adding a new Car using id: "
					+ arguments.elementAt(1));
			System.out.println("Car Location: " + arguments.elementAt(2));
			System.out.println("Add Number of Cars: " + arguments.elementAt(3));
			System.out.println("Set Price: " + arguments.elementAt(4));
			Id = getInt(arguments.elementAt(1));
			location = getString(arguments.elementAt(2));
			numCars = getInt(arguments.elementAt(3));
			price = getInt(arguments.elementAt(4));
			return new RequestMessage(Command.ADD_CARS, new Object[] { Id,
					location, numCars, price });

		case 4: // new Room
			if (arguments.size() != 5) {
				wrongNumber();
				break;
			}
			System.out.println("Adding a new Room using id: "
					+ arguments.elementAt(1));
			System.out.println("Room Location: " + arguments.elementAt(2));
			System.out
					.println("Add Number of Rooms: " + arguments.elementAt(3));
			System.out.println("Set Price: " + arguments.elementAt(4));
			Id = getInt(arguments.elementAt(1));
			location = getString(arguments.elementAt(2));
			numRooms = getInt(arguments.elementAt(3));
			price = getInt(arguments.elementAt(4));
			return new RequestMessage(Command.ADD_ROOMS, new Object[] { Id,
					location, numRooms, price });

		case 5: // new Customer
			if (arguments.size() != 2) {
				wrongNumber();
				break;
			}
			System.out.println("Adding a new Customer using id:"
					+ arguments.elementAt(1));
			Id = getInt(arguments.elementAt(1));
			return new RequestMessage(Command.NEW_CUSTOMER, new Object[] { Id });

		case 6: // delete Flight
			if (arguments.size() != 3) {
				wrongNumber();
				break;
			}
			System.out.println("Deleting a flight using id: "
					+ arguments.elementAt(1));
			System.out.println("Flight Number: " + arguments.elementAt(2));
			Id = getInt(arguments.elementAt(1));
			flightNum = getInt(arguments.elementAt(2));
			return new RequestMessage(Command.DELETE_FLIGHT, new Object[] { Id,
					flightNum });

		case 7: // delete Car
			if (arguments.size() != 3) {
				wrongNumber();
				break;
			}
			System.out
					.println("Deleting the cars from a particular location  using id: "
							+ arguments.elementAt(1));
			System.out.println("Car Location: " + arguments.elementAt(2));
			Id = getInt(arguments.elementAt(1));
			location = getString(arguments.elementAt(2));
			return new RequestMessage(Command.DELETE_CARS, new Object[] { Id,
					location });

		case 8: // delete Room
			if (arguments.size() != 3) {
				wrongNumber();
				break;
			}
			System.out
					.println("Deleting all rooms from a particular location  using id: "
							+ arguments.elementAt(1));
			System.out.println("Room Location: " + arguments.elementAt(2));
			Id = getInt(arguments.elementAt(1));
			location = getString(arguments.elementAt(2));
			return new RequestMessage(Command.DELETE_ROOMS, new Object[] { Id,
					location });

		case 9: // delete Customer
			if (arguments.size() != 3) {
				wrongNumber();
				break;
			}
			System.out
					.println("Deleting a customer from the database using id: "
							+ arguments.elementAt(1));
			System.out.println("Customer id: " + arguments.elementAt(2));
			Id = getInt(arguments.elementAt(1));
			int customer = getInt(arguments.elementAt(2));
			return new RequestMessage(Command.DELETE_CUSTOMER, new Object[] {
					Id, customer });

		case 10: // querying a flight
			if (arguments.size() != 3) {
				wrongNumber();
				break;
			}
			System.out.println("Querying a flight using id: "
					+ arguments.elementAt(1));
			System.out.println("Flight number: " + arguments.elementAt(2));
			Id = getInt(arguments.elementAt(1));
			flightNum = getInt(arguments.elementAt(2));
			return new RequestMessage(Command.QUERY_FLIGHT, new Object[] { Id,
					flightNum });

		case 11: // querying a Car Location
			if (arguments.size() != 3) {
				wrongNumber();
				break;
			}
			System.out.println("Querying a car location using id: "
					+ arguments.elementAt(1));
			System.out.println("Car location: " + arguments.elementAt(2));
			Id = getInt(arguments.elementAt(1));
			location = getString(arguments.elementAt(2));
			return new RequestMessage(Command.QUERY_CARS, new Object[] { Id,
					location });

		case 12: // querying a Room location
			if (arguments.size() != 3) {
				wrongNumber();
				break;
			}
			System.out.println("Querying a room location using id: "
					+ arguments.elementAt(1));
			System.out.println("Room location: " + arguments.elementAt(2));
			Id = getInt(arguments.elementAt(1));
			location = getString(arguments.elementAt(2));
			return new RequestMessage(Command.QUERY_ROOMS, new Object[] { Id,
					location });

		case 13: // querying Customer Information
			if (arguments.size() != 3) {
				wrongNumber();
				break;
			}
			System.out.println("Querying Customer information using id: "
					+ arguments.elementAt(1));
			System.out.println("Customer id: " + arguments.elementAt(2));
			Id = getInt(arguments.elementAt(1));
			customer = getInt(arguments.elementAt(2));
			return new RequestMessage(Command.QUERY_CUSTOMER_INFO,
					new Object[] { Id, customer });

		case 14: // querying a flight Price
			if (arguments.size() != 3) {
				wrongNumber();
				break;
			}
			System.out.println("Querying a flight Price using id: "
					+ arguments.elementAt(1));
			System.out.println("Flight number: " + arguments.elementAt(2));
			Id = getInt(arguments.elementAt(1));
			flightNum = getInt(arguments.elementAt(2));
			return new RequestMessage(Command.QUERY_FLIGHT_PRICE, new Object[] {
					Id, flightNum });

		case 15: // querying a Car Price
			if (arguments.size() != 3) {
				wrongNumber();
				break;
			}
			System.out.println("Querying a car price using id: "
					+ arguments.elementAt(1));
			System.out.println("Car location: " + arguments.elementAt(2));
			Id = getInt(arguments.elementAt(1));
			location = getString(arguments.elementAt(2));
			return new RequestMessage(Command.QUERY_CARS_PRICE, new Object[] {
					Id, location });

		case 16: // querying a Room price
			if (arguments.size() != 3) {
				wrongNumber();
				break;
			}
			System.out.println("Querying a room price using id: "
					+ arguments.elementAt(1));
			System.out.println("Room Location: " + arguments.elementAt(2));
			Id = getInt(arguments.elementAt(1));
			location = getString(arguments.elementAt(2));
			return new RequestMessage(Command.QUERY_ROOMS_PRICE, new Object[] {
					Id, location });

		case 17: // reserve a flight
			if (arguments.size() != 4) {
				wrongNumber();
				break;
			}
			System.out.println("Reserving a seat on a flight using id: "
					+ arguments.elementAt(1));
			System.out.println("Customer id: " + arguments.elementAt(2));
			System.out.println("Flight number: " + arguments.elementAt(3));
			Id = getInt(arguments.elementAt(1));
			customer = getInt(arguments.elementAt(2));
			flightNum = getInt(arguments.elementAt(3));
			return new RequestMessage(Command.RESERVE_FLIGHT, new Object[] {
					Id, customer, flightNum });

		case 18: // reserve a car
			if (arguments.size() != 4) {
				wrongNumber();
				break;
			}
			System.out.println("Reserving a car at a location using id: "
					+ arguments.elementAt(1));
			System.out.println("Customer id: " + arguments.elementAt(2));
			System.out.println("Location: " + arguments.elementAt(3));
			Id = getInt(arguments.elementAt(1));
			customer = getInt(arguments.elementAt(2));
			location = getString(arguments.elementAt(3));
			return new RequestMessage(Command.RESERVE_CAR, new Object[] { Id,
					customer, location });

		case 19: // reserve a room
			if (arguments.size() != 4) {
				wrongNumber();
				break;
			}
			System.out.println("Reserving a room at a location using id: "
					+ arguments.elementAt(1));
			System.out.println("Customer id: " + arguments.elementAt(2));
			System.out.println("Location: " + arguments.elementAt(3));
			Id = getInt(arguments.elementAt(1));
			customer = getInt(arguments.elementAt(2));
			location = getString(arguments.elementAt(3));
			return new RequestMessage(Command.RESERVE_ROOM, new Object[] { Id,
					customer, location });

		case 20: // reserve an Itinerary
			if (arguments.size() < 7) {
				wrongNumber();
				break;
			}
			System.out.println("Reserving an Itinerary using id:"
					+ arguments.elementAt(1));
			System.out.println("Customer id:" + arguments.elementAt(2));
			for (int i = 0; i < arguments.size() - 6; i++)
				System.out
						.println("Flight number" + arguments.elementAt(3 + i));
			System.out.println("Location for Car/Room booking:"
					+ arguments.elementAt(arguments.size() - 3));
			System.out.println("Car to book?:"
					+ arguments.elementAt(arguments.size() - 2));
			System.out.println("Room to book?:"
					+ arguments.elementAt(arguments.size() - 1));
			Id = getInt(arguments.elementAt(1));
			customer = getInt(arguments.elementAt(2));
			Vector flightNumbers = new Vector();
			for (int i = 0; i < arguments.size() - 6; i++)
				flightNumbers.addElement(arguments.elementAt(3 + i));
			location = getString(arguments.elementAt(arguments.size() - 3));
			car = getBoolean(arguments.elementAt(arguments.size() - 2));
			room = getBoolean(arguments.elementAt(arguments.size() - 1));
			return new RequestMessage(Command.RESERVE_ITINERARY, new Object[] {
					Id, customer, flightNumbers, location, car, room});

		case 21: // quit the client
			if (arguments.size() != 1) {
				wrongNumber();
				break;
			}
			System.out.println("Quitting client.");
			exit(0);

		case 22: // new Customer given id
			if (arguments.size() != 3) {
				wrongNumber();
				break;
			}
			System.out.println("Adding a new Customer using id:"
					+ arguments.elementAt(1) + " and cid "
					+ arguments.elementAt(2));
			Id = getInt(arguments.elementAt(1));
			Cid = getInt(arguments.elementAt(2));
			return new RequestMessage(Command.NEW_CUSTOMER_CID, new Object[] {Id, Cid});

		default:
			System.out.println("The interface does not support this command.");
			break;
		}

		return null;
	}

	public Vector parse(String command) {
		Vector arguments = new Vector();
		StringTokenizer tokenizer = new StringTokenizer(command, ",");
		String argument = "";
		while (tokenizer.hasMoreTokens()) {
			argument = tokenizer.nextToken();
			argument = argument.trim();
			arguments.add(argument);
		}
		return arguments;
	}

	public int findChoice(String argument) {
		if (argument.compareToIgnoreCase("help") == 0)
			return 1;
		else if (argument.compareToIgnoreCase("newflight") == 0)
			return 2;
		else if (argument.compareToIgnoreCase("newcar") == 0)
			return 3;
		else if (argument.compareToIgnoreCase("newroom") == 0)
			return 4;
		else if (argument.compareToIgnoreCase("newcustomer") == 0)
			return 5;
		else if (argument.compareToIgnoreCase("deleteflight") == 0)
			return 6;
		else if (argument.compareToIgnoreCase("deletecar") == 0)
			return 7;
		else if (argument.compareToIgnoreCase("deleteroom") == 0)
			return 8;
		else if (argument.compareToIgnoreCase("deletecustomer") == 0)
			return 9;
		else if (argument.compareToIgnoreCase("queryflight") == 0)
			return 10;
		else if (argument.compareToIgnoreCase("querycar") == 0)
			return 11;
		else if (argument.compareToIgnoreCase("queryroom") == 0)
			return 12;
		else if (argument.compareToIgnoreCase("querycustomer") == 0)
			return 13;
		else if (argument.compareToIgnoreCase("queryflightprice") == 0)
			return 14;
		else if (argument.compareToIgnoreCase("querycarprice") == 0)
			return 15;
		else if (argument.compareToIgnoreCase("queryroomprice") == 0)
			return 16;
		else if (argument.compareToIgnoreCase("reserveflight") == 0)
			return 17;
		else if (argument.compareToIgnoreCase("reservecar") == 0)
			return 18;
		else if (argument.compareToIgnoreCase("reserveroom") == 0)
			return 19;
		else if (argument.compareToIgnoreCase("itinerary") == 0)
			return 20;
		else if (argument.compareToIgnoreCase("quit") == 0)
			return 21;
		else if (argument.compareToIgnoreCase("newcustomerid") == 0)
			return 22;
		else
			return 666;

	}

	public void listCommands() {
		System.out
				.println("\nWelcome to the client interface provided to test your project.");
		System.out.println("Commands accepted by the interface are:");
		System.out.println("help");
		System.out
				.println("newflight\nnewcar\nnewroom\nnewcustomer\nnewcusomterid\ndeleteflight\ndeletecar\ndeleteroom");
		System.out
				.println("deletecustomer\nqueryflight\nquerycar\nqueryroom\nquerycustomer");
		System.out.println("queryflightprice\nquerycarprice\nqueryroomprice");
		System.out.println("reserveflight\nreservecar\nreserveroom\nitinerary");
		System.out.println("nquit");
		System.out
				.println("\ntype help, <commandname> for detailed info(NOTE the use of comma).");
	}

	public void listSpecific(String command) {
		System.out.print("Help on: ");
		switch (findChoice(command)) {
		case 1:
			System.out.println("Help");
			System.out
					.println("\nTyping help on the prompt gives a list of all the commands available.");
			System.out
					.println("Typing help, <commandname> gives details on how to use the particular command.");
			break;

		case 2: // new flight
			System.out.println("Adding a new Flight.");
			System.out.println("Purpose:");
			System.out.println("\tAdd information about a new flight.");
			System.out.println("\nUsage:");
			System.out
					.println("\tnewflight,<id>,<flightnumber>,<flightSeats>,<flightprice>");
			break;

		case 3: // new Car
			System.out.println("Adding a new Car.");
			System.out.println("Purpose:");
			System.out.println("\tAdd information about a new car location.");
			System.out.println("\nUsage:");
			System.out
					.println("\tnewcar,<id>,<location>,<numberofcars>,<pricepercar>");
			break;

		case 4: // new Room
			System.out.println("Adding a new Room.");
			System.out.println("Purpose:");
			System.out.println("\tAdd information about a new room location.");
			System.out.println("\nUsage:");
			System.out
					.println("\tnewroom,<id>,<location>,<numberofrooms>,<priceperroom>");
			break;

		case 5: // new Customer
			System.out.println("Adding a new Customer.");
			System.out.println("Purpose:");
			System.out
					.println("\tGet the system to provide a new customer id. (same as adding a new customer)");
			System.out.println("\nUsage:");
			System.out.println("\tnewcustomer,<id>");
			break;

		case 6: // delete Flight
			System.out.println("Deleting a flight");
			System.out.println("Purpose:");
			System.out.println("\tDelete a flight's information.");
			System.out.println("\nUsage:");
			System.out.println("\tdeleteflight,<id>,<flightnumber>");
			break;

		case 7: // delete Car
			System.out.println("Deleting a Car");
			System.out.println("Purpose:");
			System.out.println("\tDelete all cars from a location.");
			System.out.println("\nUsage:");
			System.out.println("\tdeletecar,<id>,<location>,<numCars>");
			break;

		case 8: // delete Room
			System.out.println("Deleting a Room");
			System.out.println("\nPurpose:");
			System.out.println("\tDelete all rooms from a location.");
			System.out.println("Usage:");
			System.out.println("\tdeleteroom,<id>,<location>,<numRooms>");
			break;

		case 9: // delete Customer
			System.out.println("Deleting a Customer");
			System.out.println("Purpose:");
			System.out.println("\tRemove a customer from the database.");
			System.out.println("\nUsage:");
			System.out.println("\tdeletecustomer,<id>,<customerid>");
			break;

		case 10: // querying a flight
			System.out.println("Querying flight.");
			System.out.println("Purpose:");
			System.out
					.println("\tObtain Seat information about a certain flight.");
			System.out.println("\nUsage:");
			System.out.println("\tqueryflight,<id>,<flightnumber>");
			break;

		case 11: // querying a Car Location
			System.out.println("Querying a Car location.");
			System.out.println("Purpose:");
			System.out
					.println("\tObtain number of cars at a certain car location.");
			System.out.println("\nUsage:");
			System.out.println("\tquerycar,<id>,<location>");
			break;

		case 12: // querying a Room location
			System.out.println("Querying a Room Location.");
			System.out.println("Purpose:");
			System.out
					.println("\tObtain number of rooms at a certain room location.");
			System.out.println("\nUsage:");
			System.out.println("\tqueryroom,<id>,<location>");
			break;

		case 13: // querying Customer Information
			System.out.println("Querying Customer Information.");
			System.out.println("Purpose:");
			System.out.println("\tObtain information about a customer.");
			System.out.println("\nUsage:");
			System.out.println("\tquerycustomer,<id>,<customerid>");
			break;

		case 14: // querying a flight for price
			System.out.println("Querying flight.");
			System.out.println("Purpose:");
			System.out
					.println("\tObtain price information about a certain flight.");
			System.out.println("\nUsage:");
			System.out.println("\tqueryflightprice,<id>,<flightnumber>");
			break;

		case 15: // querying a Car Location for price
			System.out.println("Querying a Car location.");
			System.out.println("Purpose:");
			System.out
					.println("\tObtain price information about a certain car location.");
			System.out.println("\nUsage:");
			System.out.println("\tquerycarprice,<id>,<location>");
			break;

		case 16: // querying a Room location for price
			System.out.println("Querying a Room Location.");
			System.out.println("Purpose:");
			System.out
					.println("\tObtain price information about a certain room location.");
			System.out.println("\nUsage:");
			System.out.println("\tqueryroomprice,<id>,<location>");
			break;

		case 17: // reserve a flight
			System.out.println("Reserving a flight.");
			System.out.println("Purpose:");
			System.out.println("\tReserve a flight for a customer.");
			System.out.println("\nUsage:");
			System.out
					.println("\treserveflight,<id>,<customerid>,<flightnumber>");
			break;

		case 18: // reserve a car
			System.out.println("Reserving a Car.");
			System.out.println("Purpose:");
			System.out
					.println("\tReserve a given number of cars for a customer at a particular location.");
			System.out.println("\nUsage:");
			System.out
					.println("\treservecar,<id>,<customerid>,<location>,<nummberofCars>");
			break;

		case 19: // reserve a room
			System.out.println("Reserving a Room.");
			System.out.println("Purpose:");
			System.out
					.println("\tReserve a given number of rooms for a customer at a particular location.");
			System.out.println("\nUsage:");
			System.out
					.println("\treserveroom,<id>,<customerid>,<location>,<nummberofRooms>");
			break;

		case 20: // reserve an Itinerary
			System.out.println("Reserving an Itinerary.");
			System.out.println("Purpose:");
			System.out
					.println("\tBook one or more flights.Also book zero or more cars/rooms at a location.");
			System.out.println("\nUsage:");
			System.out
					.println("\titinerary,<id>,<customerid>,<flightnumber1>....<flightnumberN>,<LocationToBookCarsOrRooms>,<NumberOfCars>,<NumberOfRoom>");
			break;

		case 21: // quit the client
			System.out.println("Quitting client.");
			System.out.println("Purpose:");
			System.out.println("\tExit the client application.");
			System.out.println("\nUsage:");
			System.out.println("\tquit");
			break;

		case 22: // new customer with id
			System.out.println("Create new customer providing an id");
			System.out.println("Purpose:");
			System.out.println("\tCreates a new customer with the id provided");
			System.out.println("\nUsage:");
			System.out.println("\tnewcustomerid, <id>, <customerid>");
			break;

		default:
			System.out.println(command);
			System.out.println("The interface does not support this command.");
			break;
		}
	}

	public void wrongNumber() {
		System.out
				.println("The number of arguments provided in this command are wrong.");
		System.out
				.println("Type help, <commandname> to check usage of this command.");
	}

	public int getInt(Object temp) throws Exception {
		try {
			return (new Integer((String) temp)).intValue();
		} catch (Exception e) {
			throw e;
		}
	}

	public boolean getBoolean(Object temp) throws Exception {
		try {
			return (new Boolean((String) temp)).booleanValue();
		} catch (Exception e) {
			throw e;
		}
	}

	public String getString(Object temp) throws Exception {
		try {
			return (String) temp;
		} catch (Exception e) {
			throw e;
		}
	}

	public static void exit(int status) {
		try {
			if (out != null)
				out.close();
			if (in != null)
				in.close();
			if (stdIn != null)
				stdIn.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.exit(status);
	}
}
