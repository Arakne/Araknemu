package fr.quatrevieux.araknemu.game.event.listener.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.common.PlayerXpChanged;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SendPlayerXpTest extends GameBaseCase {
    private SendPlayerXp listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendPlayerXp(gamePlayer(true));

        requestStack.clear();
    }

    @Test
    void onPlayerXpChanged() throws SQLException, ContainerException {
        listener.on(new PlayerXpChanged());

        requestStack.assertLast(new Stats(gamePlayer()));
    }
}