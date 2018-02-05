package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextNotFoundException;

import java.util.*;

/**
 * Simple context
 */
final public class SimpleContext implements Context {
    final private Context parent;

    final private Map<String, Command> commands = new HashMap<>();
    final private Map<String, Context> children = new HashMap<>();

    public SimpleContext(Context parent) {
        this.parent = parent;
    }

    @Override
    public Command command(String name) throws CommandNotFoundException {
        if (commands.containsKey(name)) {
            return commands.get(name);
        }

        return parent.command(name);
    }

    @Override
    public Collection<Command> commands() {
        Collection<Command> allCommands = new ArrayList<>(commands.values());

        allCommands.addAll(parent.commands());

        return allCommands;
    }

    @Override
    public Context child(String name) throws ContextNotFoundException {
        if (children.containsKey(name)) {
            return children.get(name);
        }

        return parent.child(name);
    }

    /**
     * Add a new command to the context
     */
    public SimpleContext add(Command command) {
        commands.put(command.name(), command);

        return this;
    }

    /**
     * Add a new child context
     *
     * @param name The child name
     * @param child The child
     */
    public SimpleContext add(String name, Context child) {
        children.put(name, child);

        return this;
    }
}
