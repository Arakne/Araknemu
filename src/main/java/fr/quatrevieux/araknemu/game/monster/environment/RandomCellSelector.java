package fr.quatrevieux.araknemu.game.monster.environment;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.util.RandomUtil;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Select a random cell from all free cells of the map
 */
final public class RandomCellSelector implements SpawnCellSelector {
    final static private RandomUtil RANDOM = RandomUtil.createShared();

    private ExplorationMap map;
    private int[] availableCells;

    @Override
    public void setMap(ExplorationMap map) {
        this.map = map;

        List<Integer> freeCells = new ArrayList<>();

        for (int cellId = 0; cellId < map.size(); ++cellId) {
            final ExplorationMapCell cell = map.get(cellId);

            if (cell.free()) {
                freeCells.add(cellId);
            }
        }

        availableCells = ArrayUtils.toPrimitive(RANDOM.shuffle(freeCells).toArray(new Integer[0]));
    }

    @Override
    public ExplorationMapCell cell() {
        final int offset = RANDOM.integer(availableCells.length);

        for (int i = 0; i < availableCells.length; ++i) {
            int cellId = availableCells[(i + offset) % availableCells.length];

            final ExplorationMapCell cell = map.get(cellId);

            if (cell.free()) {
                return cell;
            }
        }

        throw new IllegalStateException("Cannot found a free cell on map " + map.id());
    }
}
