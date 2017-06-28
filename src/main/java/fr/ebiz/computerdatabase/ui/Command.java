package fr.ebiz.computerdatabase.ui;

import static fr.ebiz.computerdatabase.ui.StringUtils.*;

public enum Command {
    HELP("help", "Show this help"), QUIT("quit", "Exit the application"), LIST_COMPUTERS("list-computers", "List the computers in the database"), LIST_COMPANIES("list-companies", "List the companies in the database"), ADD_COMPUTER("add-computer", "Add a computer"), UPDATE_COMPUTER("update-computer", "Update a computer"), DELETE_COMPUTER("delete-computer", "Delete a computer"), SHOW_COMPUTER("show-computer", "Computer detail");

    private final String commandString;
    private final String helpMessage;

    Command(String commandString, String helpMessage) {
        this.commandString = commandString;
        this.helpMessage = helpMessage;
    }

    public String getCommandString() {
        return commandString;
    }

    public String getHelpMessage() {
        return helpMessage;
    }

    public static Command getCommand(String commandString) {
        if (!isNotBlank(commandString)) {
            return null;
        }
        for (Command command : values()) {
            if (command.getCommandString().equals(cleanString(commandString))) {
                return command;
            }
        }
        return null;
    }

}
