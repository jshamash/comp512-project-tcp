package messages.requests;

import messages.Command;
import messages.RequestMessage;

public class NewCustomer extends RequestMessage {

	int id;
	
	public NewCustomer(int id) {
		super(Command.NEW_CUSTOMER, new Object[] {id});
		this.id = id;
	}
	
	public int getID() { return id; }

}
