package fr.quatrevieux.araknemu.game.admin.exception;

/**
 * Base exception for commands
 */
public class CommandException extends AdminException {
    final private String command;

    public CommandException(String command) {
        this.command = command;
    }

    public CommandException(String command, String message) {
        super(message);
        this.command = command;
    }

    public CommandException(String command, String message, Throwable cause) {
        super(message, cause);
        this.command = command;
    }

    public CommandException(String command, Throwable cause) {
        super(cause);
        this.command = command;
    }

    public String command() {
        return command;
    }
}
