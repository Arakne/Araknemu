package fr.quatrevieux.araknemu.network.game.out.emote;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class PlayerOrientationTest extends GameBaseCase {
    @Test
    void generate() throws SQLException, ContainerException {
        ExplorationPlayer exploration = explorationPlayer();

        exploration.setOrientation(Direction.WEST);

        assertEquals("eD1|4", new PlayerOrientation(exploration).toString());
    }
}
