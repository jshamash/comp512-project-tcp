package messages;

/**
 * Superclass for all messages
 * @author Jake
 *
 */
public class RequestMessage implements Message {
	
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
}
