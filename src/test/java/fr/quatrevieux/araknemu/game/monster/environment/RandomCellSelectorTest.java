package fr.quatrevieux.araknemu.game.monster.environment;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;
import fr.quatrevieux.araknemu.game.exploration.creature.Operation;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RandomCellSelectorTest extends GameBaseCase {
    private RandomCellSelector selector;
    private ExplorationMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();
        selector = new RandomCellSelector();
        selector.setMap(map = container.get(ExplorationMapService.class).load(10340));
    }

    @Test
    void cell() {
        assertNotEquals(selector.cell(), selector.cell());
        assertTrue(selector.cell().free());
    }

    @Test
    void cellWithoutFreePlace() {
        for (int cellId = 0; cellId < map.size(); ++cellId) {
            map.add(new FakeCreature(map.get(cellId)));
        }

        assertThrows(IllegalStateException.class, () -> selector.cell());
    }

    class FakeCreature implements ExplorationCreature {
        final private ExplorationMapCell cell;

        public FakeCreature(ExplorationMapCell cell) {
            this.cell = cell;
        }

        @Override
        public void apply(Operation operation) {

        }

        @Override
        public Sprite sprite() {
            return null;
        }

        @Override
        public int id() {
            return 1000 + cell.id();
        }

        @Override
        public ExplorationMapCell cell() {
            return cell;
        }

        @Override
        public Direction orientation() {
            return null;
        }
    }
}
