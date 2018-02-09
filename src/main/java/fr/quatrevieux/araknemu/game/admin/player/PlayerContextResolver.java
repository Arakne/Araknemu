package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.game.admin.Context;
import fr.quatrevieux.araknemu.game.admin.ContextResolver;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;

import java.util.NoSuchElementException;

/**
 * Context resolver for player
 */
final public class PlayerContextResolver implements ContextResolver {
    final private PlayerService service;
    final private ContextResolver accountContextResolver;
    final private ItemService itemService;

    public PlayerContextResolver(PlayerService service, ContextResolver accountContextResolver, ItemService itemService) {
        this.service = service;
        this.accountContextResolver = accountContextResolver;
        this.itemService = itemService;
    }

    @Override
    public Context resolve(Context globalContext, Object argument) throws ContextException {
        if (argument instanceof GamePlayer) {
            return resolve(globalContext, GamePlayer.class.cast(argument));
        } else if (argument instanceof String) {
            return resolve(globalContext, String.class.cast(argument));
        }

        throw new ContextException("Invalid argument : " + argument);
    }

    @Override
    public String type() {
        return "player";
    }

    private PlayerContext resolve(Context globalContext, GamePlayer player) throws ContextException {
        return new PlayerContext(
            player,
            accountContextResolver.resolve(globalContext, player.account()),
            itemService
        );
    }

    private PlayerContext resolve(Context globalContext, String name) throws ContextException {
        try {
            return resolve(globalContext, service.get(name));
        } catch (NoSuchElementException e) {
            throw new ContextException("Cannot found the player " + name);
        }
    }
}
