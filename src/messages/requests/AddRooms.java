package messages.requests;

import messages.Command;
import messages.RequestMessage;

public class AddRooms extends RequestMessage {

	int id;
	String location;
	int numRooms;
	int price;
	
	public AddRooms(int id, String location, int numRooms, int price) {
		super(Command.ADD_ROOMS, new Object[] {id, location, numRooms, price});
		
		this.id = id;
		this.location = location;
		this.numRooms = numRooms;
		this.price = price;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @return the numRooms
	 */
	public int getNumRooms() {
		return numRooms;
	}

	/**
	 * @return the price
	 */
	public int getPrice() {
		return price;
	}

}
