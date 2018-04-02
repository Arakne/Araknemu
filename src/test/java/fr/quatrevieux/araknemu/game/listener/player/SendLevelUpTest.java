package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.experience.event.PlayerLevelUp;
import fr.quatrevieux.araknemu.network.game.out.account.NewPlayerLevel;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class SendLevelUpTest extends GameBaseCase {
    private SendLevelUp listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendLevelUp(gamePlayer(true));

        requestStack.clear();
    }

    @Test
    void onLevelUp() throws SQLException, ContainerException {
        listener.on(new PlayerLevelUp(50));

        requestStack.assertAll(
            new NewPlayerLevel(50),
            new Stats(gamePlayer())
        );
    }
}