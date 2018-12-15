package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.event.GameJoined;
import fr.quatrevieux.araknemu.network.game.out.account.AlterRestrictions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class InitializeRestrictionsTest extends GameBaseCase {
    private InitializeRestrictions listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new InitializeRestrictions(gamePlayer());
    }

    @Test
    void onGameJoined() throws SQLException, ContainerException {
        listener.on(new GameJoined());

        requestStack.assertLast(new AlterRestrictions(gamePlayer().restrictions()));
    }
}
