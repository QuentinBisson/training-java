package fr.ebiz.computerdatabase.ui.cli;

import fr.ebiz.computerdatabase.utils.StringUtils;

public enum Command {
    HELP("help", "Show this help"), QUIT("quit", "Exit the application"), LIST_COMPUTERS("list-computers", "List the computers in the database"), LIST_COMPANIES("list-companies", "List the companies in the database"), ADD_COMPUTER("add-computer", "Add a computer"), UPDATE_COMPUTER("update-computer", "Update a computer"), DELETE_COMPANY("delete-company", "Delete a company"), DELETE_COMPUTER("delete-computer", "Delete a computer"), SHOW_COMPUTER("show-computer", "Computer detail");

    private final String commandString;
    private final String helpMessage;

    /**
     * Constructor.
     *
     * @param commandString The command string used in the CLI
     * @param helpMessage   The help message describing what the command is for
     */
    Command(String commandString, String helpMessage) {
        this.commandString = commandString;
        this.helpMessage = helpMessage;
    }

    /**
     * Get the command from a String.
     *
     * @param commandString The command string to check
     * @return The existing command or null
     */
    public static Command getCommand(String commandString) {
        if (StringUtils.isBlank(commandString)) {
            return null;
        }
        for (Command command : values()) {
            if (command.getCommandString().equals(StringUtils.cleanString(commandString))) {
                return command;
            }
        }
        return null;
    }

    public String getCommandString() {
        return commandString;
    }

    public String getHelpMessage() {
        return helpMessage;
    }

}
