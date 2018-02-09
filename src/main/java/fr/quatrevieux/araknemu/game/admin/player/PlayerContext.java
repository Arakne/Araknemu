package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.game.admin.*;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextNotFoundException;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

import java.util.Collection;

/**
 * Context for a player
 */
final public class PlayerContext implements Context {
    final private GamePlayer player;
    final private Context accountContext;
    final private ItemService itemService;

    final private Context context;

    public PlayerContext(GamePlayer player, Context accountContext, ItemService itemService) throws ContextException {
        this.player = player;
        this.accountContext = accountContext;
        this.itemService = itemService;

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
            .add(new GetItem(player, itemService))
            .add("account", accountContext)
        ;
    }
}
