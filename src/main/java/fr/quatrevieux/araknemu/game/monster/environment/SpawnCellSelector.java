package fr.quatrevieux.araknemu.game.monster.environment;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupPosition;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;

/**
 * Strategy for select the group spawn cell
 */
public interface SpawnCellSelector {
    /**
     * Set the spawn map
     */
    public void setMap(ExplorationMap map);

    /**
     * Select the spawn cell
     */
    public ExplorationMapCell cell();

    /**
     * Create the cell selector for the given position
     *
     * If the cell is fixed (not -1 on {@link MonsterGroupPosition#position() cell}), a {@link FixedCellSelector} is returned
     * If not, {@link RandomCellSelector} is returned
     *
     * @param position The monster group position
     */
    static public SpawnCellSelector forPosition(Position position) {
        if (position.cell() == -1) {
            return new RandomCellSelector();
        }

        return new FixedCellSelector(position);
    }
}
