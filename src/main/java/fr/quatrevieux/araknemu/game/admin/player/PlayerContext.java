package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.game.admin.*;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextNotFoundException;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

import java.util.Collection;

/**
 * Context for a player
 */
final public class PlayerContext implements Context {
    final private GamePlayer player;
    final private Context accountContext;

    final private Context context;

    public PlayerContext(GamePlayer player, Context accountContext) throws ContextException {
        this.player = player;
        this.accountContext = accountContext;

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

    private Context configure() throws ContextException {
        return new SimpleContext(accountContext)
            .add(new Info(player))
            .add("account", accountContext)
        ;
    }
}
