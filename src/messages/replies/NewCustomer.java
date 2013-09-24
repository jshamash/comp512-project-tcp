package messages.replies;

import messages.Command;
import messages.ReplyMessage;

public class NewCustomer extends ReplyMessage {

	int cid;
	public NewCustomer(int cid) {
		super(Command.NEW_CUSTOMER, cid);
		
		this.cid = cid;
	}
	/**
	 * @return the cid
	 */
	public int getCid() {
		return cid;
	}

}
