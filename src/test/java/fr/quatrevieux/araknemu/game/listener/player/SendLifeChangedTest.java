package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.characteristic.event.LifeChanged;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class SendLifeChangedTest extends GameBaseCase {
    private SendLifeChanged listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendLifeChanged(
            gamePlayer()
        );

        requestStack.clear();
    }

    @Test
    void onLifeChangedNoDiff() {
        listener.on(new LifeChanged(100, 100));

        requestStack.assertEmpty();
    }

    @Test
    void onLifeChangedNegative() throws SQLException, ContainerException {
        listener.on(new LifeChanged(100, 50));

        requestStack.assertAll(
            new Stats(gamePlayer().properties())
        );
    }

    @Test
    void onLifeChangedPositive() throws SQLException, ContainerException {
        listener.on(new LifeChanged(100, 150));

        requestStack.assertAll(
            new Stats(gamePlayer().properties()),
            Information.heal(50)
        );
    }
}