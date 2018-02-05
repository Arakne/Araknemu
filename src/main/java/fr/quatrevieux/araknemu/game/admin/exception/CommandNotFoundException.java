package fr.quatrevieux.araknemu.game.admin.exception;

/**
 * The command is not found
 */
public class CommandNotFoundException extends CommandException {
    public CommandNotFoundException(String command) {
        super(command);
    }
}
