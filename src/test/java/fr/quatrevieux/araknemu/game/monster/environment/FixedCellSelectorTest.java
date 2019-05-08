package fr.quatrevieux.araknemu.game.monster.environment;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FixedCellSelectorTest extends GameBaseCase {
    private FixedCellSelector selector;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        selector = new FixedCellSelector(new Position(10340, 123));
    }

    @Test
    void cell() {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);
        selector.setMap(map);

        assertEquals(map.get(123), selector.cell());
    }
}
