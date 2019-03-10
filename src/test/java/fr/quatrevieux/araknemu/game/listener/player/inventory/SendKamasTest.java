package fr.quatrevieux.araknemu.game.listener.player.inventory;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.inventory.event.KamasChanged;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class SendKamasTest extends GameBaseCase {
    private SendKamas listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendKamas(gamePlayer());
    }

    @Test
    void onKamasChanged() throws SQLException {
        listener.on(new KamasChanged(0, 15225));

        requestStack.assertLast(new Stats(gamePlayer().properties()));
    }
}
