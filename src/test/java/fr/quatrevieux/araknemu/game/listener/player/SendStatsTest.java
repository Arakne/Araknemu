package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.characteristic.event.CharacteristicsChanged;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class SendStatsTest extends GameBaseCase {
    private SendStats listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendStats(gamePlayer(true));
        requestStack.clear();
    }

    @Test
    void onCharacteristicsChanged() throws SQLException, ContainerException {
        listener.on(new CharacteristicsChanged());

        requestStack.assertLast(
            new Stats(gamePlayer())
        );
    }
}
