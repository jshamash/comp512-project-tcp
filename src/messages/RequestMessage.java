package messages;

import java.io.Serializable;

/**
 * Superclass for all messages
 * @author Jake
 *
 */
public class RequestMessage implements Message, Serializable {
	
	private static final long serialVersionUID = -7115353980562169097L;
	Command command;	// header
	Object[] params;	// body
	
	public RequestMessage(Command command, Object[] params) {
		this.command = command;
		this.params = params;
	}
	
	/**
	 * Gets the command associated with this message.
	 * @return The command to be executed
	 */
	public Command getCommand() {
		return command;
	}
	
	/**
	 * Gets the parameters associated with this command.
	 * @return The parameters
	 */
	public Object[] getParams() {
		return params;
	}
	
	public String toString() {
		String lReturn = "Request message: [Command: " + command.toString() + ", params: ";
		for (Object param : params) {
			lReturn += param + ",";
		}
		return lReturn + "]";
		
	}
}
