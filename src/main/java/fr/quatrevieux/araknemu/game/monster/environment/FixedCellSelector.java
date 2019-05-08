package fr.quatrevieux.araknemu.game.monster.environment;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;

/**
 * Cell selector for fixed monster group
 */
final public class FixedCellSelector implements SpawnCellSelector {
    final private Position position;
    private ExplorationMap map;

    public FixedCellSelector(Position position) {
        this.position = position;
    }

    @Override
    public void setMap(ExplorationMap map) {
        this.map = map;
    }

    @Override
    public ExplorationMapCell cell() {
        return map.get(position.cell());
    }
}
