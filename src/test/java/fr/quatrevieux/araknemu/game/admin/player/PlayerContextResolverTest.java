package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.Context;
import fr.quatrevieux.araknemu.game.admin.NullContext;
import fr.quatrevieux.araknemu.game.admin.account.AccountContext;
import fr.quatrevieux.araknemu.game.admin.account.AccountContextResolver;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.game.world.item.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class PlayerContextResolverTest extends GameBaseCase {
    private PlayerContextResolver resolver;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.use(Player.class);

        resolver = new PlayerContextResolver(
            container.get(PlayerService.class),
            container.get(AccountContextResolver.class),
            container.get(ItemService.class)
        );
    }

    @Test
    void resolveByGamePlayer() throws SQLException, ContainerException, ContextException {
        Context context = resolver.resolve(
            new NullContext(),
            gamePlayer()
        );

        assertInstanceOf(PlayerContext.class, context);
        assertInstanceOf(AccountContext.class, context.child("account"));
    }

    @Test
    void resolveByName() throws SQLException, ContainerException, ContextException {
        gamePlayer(true);

        Context context = resolver.resolve(new NullContext(), "Bob");

        assertInstanceOf(PlayerContext.class, context);
        assertInstanceOf(AccountContext.class, context.child("account"));
    }

    @Test
    void resolveInvalidArgument() {
        assertThrows(ContextException.class, () -> resolver.resolve(new NullContext(), new Object()));
    }

    @Test
    void resolvePlayerNotFound() {
        assertThrows(ContextException.class, () -> resolver.resolve(new NullContext(), "notFound"), "Cannot found the player notFound");
    }
}
