package messages.replies;

import messages.Command;
import messages.ReplyMessage;

public class NewCustomerCID extends ReplyMessage {

	boolean status;
	public NewCustomerCID(boolean status) {
		super(Command.NEW_CUSTOMER_CID, status);
		
		this.status = status;
	}
	/**
	 * @return the status
	 */
	public boolean isStatus() {
		return status;
	}

}
