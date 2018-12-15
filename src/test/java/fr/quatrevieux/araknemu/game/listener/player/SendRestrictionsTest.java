package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.event.RestrictionsChanged;
import fr.quatrevieux.araknemu.network.game.out.account.AlterRestrictions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class SendRestrictionsTest extends GameBaseCase {
    private SendRestrictions listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendRestrictions(gamePlayer());
    }

    @Test
    void onRestrictionsChanged() throws SQLException, ContainerException {
        listener.on(new RestrictionsChanged(gamePlayer().restrictions()));

        requestStack.assertLast(new AlterRestrictions(gamePlayer().restrictions()));
    }
}
