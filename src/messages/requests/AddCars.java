package messages.requests;

import messages.Command;
import messages.RequestMessage;

public class AddCars extends RequestMessage {

	int id;
	String location;
	int numCars;
	int price;
	
	public AddCars(int id, String location, int numCars, int price) {
		super(Command.ADD_CARS, new Object[] {id, location, numCars, price});
		this.id = id;
		this.location = location;
		this.numCars = numCars;
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
	 * @return the numCars
	 */
	public int getNumCars() {
		return numCars;
	}

	/**
	 * @return the price
	 */
	public int getPrice() {
		return price;
	}
	
	

}
