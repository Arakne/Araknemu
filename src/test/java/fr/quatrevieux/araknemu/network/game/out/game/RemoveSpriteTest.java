package fr.quatrevieux.araknemu.network.game.out.game;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class RemoveSpriteTest extends GameBaseCase {
    @Test
    void generate() throws SQLException, ContainerException {
        assertEquals(
            "GM|-1",
            new RemoveSprite(explorationPlayer().sprite()).toString()
        );
    }
}
