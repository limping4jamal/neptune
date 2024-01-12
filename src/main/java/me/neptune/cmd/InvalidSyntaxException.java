/**
 * A class to represent an exception thrown when a command is typed with invalid syntax.
 */
package me.neptune.cmd;

import me.neptune.Neptune;

public class InvalidSyntaxException extends CommandException {
	private static final long serialVersionUID = 1L;
	
	public InvalidSyntaxException(Command cmd) {
		super(cmd);
	}

	@Override
	public void PrintToChat() {
		CommandManager.sendChatMessage("Invalid Usage! Usage: " + Neptune.PREFIX + " " + cmd.getName() + " " + cmd.getSyntax());
	}
}
