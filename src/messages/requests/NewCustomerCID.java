package messages.requests;

import messages.Command;
import messages.RequestMessage;

public class NewCustomerCID extends RequestMessage {

	int id, cid;

	public NewCustomerCID(int id, int cid) {
		super(Command.NEW_CUSTOMER_CID, new Object[] { id, cid });
		this.id = id;
		this.cid = cid;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the cid
	 */
	public int getCid() {
		return cid;
	}

}
