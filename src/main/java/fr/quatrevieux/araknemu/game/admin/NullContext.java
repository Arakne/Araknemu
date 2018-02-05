package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextNotFoundException;

import java.util.Collection;
import java.util.Collections;

/**
 * Null object for context
 */
final public class NullContext implements Context {
    @Override
    public Command command(String name) throws CommandNotFoundException {
        throw new CommandNotFoundException(name);
    }

    @Override
    public Collection<Command> commands() {
        return Collections.emptyList();
    }

    @Override
    public Context child(String name) throws ContextNotFoundException {
        throw new ContextNotFoundException(name);
    }
}
