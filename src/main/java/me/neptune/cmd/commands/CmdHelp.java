package me.neptune.cmd.commands;

import me.neptune.Neptune;
import me.neptune.cmd.Command;
import me.neptune.cmd.CommandManager;
import me.neptune.module.Module;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class CmdHelp extends Command {

	int indexesPerPage = 5;

	public CmdHelp() {
		super("help", "Shows the avaiable commands.", "[page, module]");
	}

	@Override
	public void runCommand(String[] parameters) {
		if (parameters.length <= 0) {
			ShowCommands(1);
		} else if (StringUtils.isNumeric(parameters[0])) {
			int page = Integer.parseInt(parameters[0]);
			ShowCommands(page);
		} else {
			Module module = Neptune.MODULE.getModuleByName(parameters[0]);
			if (module == null) {
				CommandManager.sendChatMessage("Could not find Module '" + parameters[0] + "'.");
			} else {
				CommandManager.sendChatMessage("------------ " + module.getName() + "Help ------------");
				CommandManager.sendChatMessage("Name: " + module.getName());
				CommandManager.sendChatMessage("Description: " + module.getDescription());
				CommandManager.sendChatMessage("Keybind: " + module.getBind().getBind() + " " + module.getBind().getKey());
			}
		}

	}

	private void ShowCommands(int page) {
		CommandManager.sendChatMessage("------------ Help [Page " + page + " of 5] ------------");
		CommandManager.sendChatMessage("Use " + Neptune.PREFIX + " help [n] to get page n of help.");

		// Fetch the commands and dislays their syntax on the screen.
		HashMap<String, Command> commands = Neptune.COMMAND.getCommands();
		Set<String> keySet = commands.keySet();
		ArrayList<String> listOfCommands = new ArrayList<>(keySet);
		 
		for (int i = (page - 1) * indexesPerPage; i <= (page * indexesPerPage); i++) {
			if (i >= 0 && i < Neptune.COMMAND.getNumOfCommands()) {
				CommandManager.sendChatMessage(" " + Neptune.PREFIX + " " + listOfCommands.get(i));
			}
		}
	}

	@Override
	public String[] getAutocorrect(int count) {
		return null;
	}

}
