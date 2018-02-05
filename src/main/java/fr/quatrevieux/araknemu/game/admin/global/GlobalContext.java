package fr.quatrevieux.araknemu.game.admin.global;

import fr.quatrevieux.araknemu.game.admin.*;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextNotFoundException;

import java.util.Collection;

/**
 * Global context.
 * This context should be available in all other contexts
 */
final public class GlobalContext implements Context {
    final private AdminUser user;

    final private Context context;

    public GlobalContext(AdminUser user) {
        this.user = user;

        context = configure();
    }

    @Override
    public Command command(String name) throws CommandNotFoundException {
        return context.command(name);
    }

    @Override
    public Collection<Command> commands() {
        return context.commands();
    }

    @Override
    public Context child(String name) throws ContextNotFoundException {
        return context.child(name);
    }

    private Context configure() {
        return new SimpleContext(new NullContext())
            .add(new Echo())
        ;
    }
}
