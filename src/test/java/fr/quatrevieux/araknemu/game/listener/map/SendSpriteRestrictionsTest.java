package fr.quatrevieux.araknemu.game.listener.map;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.event.RestrictionsChanged;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class SendSpriteRestrictionsTest extends GameBaseCase {
    private SendSpriteRestrictions listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendSpriteRestrictions(explorationPlayer().map());
        requestStack.clear();
    }

    @Test
    void onRestrictionsChanged() throws SQLException, ContainerException {
        listener.on(new RestrictionsChanged(explorationPlayer(), explorationPlayer().restrictions()));

        requestStack.assertLast(new AddSprites(Collections.singleton(explorationPlayer().sprite())));
    }
}