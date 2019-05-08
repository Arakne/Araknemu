package fr.quatrevieux.araknemu.game.monster.environment;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.data.value.Position;
import org.junit.jupiter.api.Test;

class SpawnCellSelectorTest extends TestCase {
    @Test
    void forPosition() {
        assertInstanceOf(RandomCellSelector.class, SpawnCellSelector.forPosition(new Position(10340, -1)));
        assertInstanceOf(FixedCellSelector.class, SpawnCellSelector.forPosition(new Position(10340, 123)));
    }
}
