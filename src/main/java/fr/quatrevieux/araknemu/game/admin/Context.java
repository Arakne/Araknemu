package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextNotFoundException;

import java.util.Collection;

/**
 * Context for console
 */
public interface Context {
    /**
     * Get a command by its name
     *
     * @param name The command name
     *
     * @return The command
     *
     * @throws CommandNotFoundException When the asked command cannot be found
     */
    public Command command(String name) throws CommandNotFoundException;

    /**
     * Get all available commands
     */
    public Collection<Command> commands();

    /**
     * Get a child context
     *
     * @param name The context name
     */
    public Context child(String name) throws ContextNotFoundException;
}
