package fr.quatrevieux.araknemu.game.monster.environment;

import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupPosition;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroupFactory;
import fr.quatrevieux.araknemu.util.RandomUtil;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Handle a monster group configuration on map and cell
 */
final public class LivingMonsterGroupPosition {
    final static private RandomUtil RANDOM = RandomUtil.createShared();

    final private MonsterGroupPosition position;
    final private MonsterGroupData data;
    final private MonsterGroupFactory factory;

    private ExplorationMap map;

    public LivingMonsterGroupPosition(MonsterGroupPosition position, MonsterGroupData data, MonsterGroupFactory factory) {
        this.position = position;
        this.data = data;
        this.factory = factory;
    }

    /**
     * Link the group to the map and populate (spawn) with configured group data
     * This method should be called once, when exploration map is loaded
     *
     * @see fr.quatrevieux.araknemu.game.exploration.map.event.MapLoaded
     */
    public void populate(ExplorationMap map) {
        this.map = map;

        for (long i = groupStream().count(); i < data.maxCount(); ++i) {
            spawn();
        }
    }

    /**
     * Spawn a new group on the map
     *
     * Note: this method will not check the max count of monsters : if called manually, the group count can exceed max count
     */
    public void spawn() {
        map.add(factory.create(data, spawnCell()));
    }

    /**
     * Get list of available monster groups on the map
     */
    public List<MonsterGroup> available() {
        return groupStream().collect(Collectors.toList());
    }

    /**
     * Find the group spawn cell
     *
     * If the cell is fixed (not -1 on {@link MonsterGroupPosition#position() cell}), this cell is returned
     * If not, a random free (without creatures, objects, and walkable) cell is returned
     *
     * @see fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell#free()
     */
    private int spawnCell() {
        if (position.position().cell() != -1) {
            return position.position().cell();
        }

        for (int i = 0; i < 128; ++i) {
            int cellId = RANDOM.integer(map.size());

            if (map.get(cellId).free()) {
                return cellId;
            }
        }

        throw new IllegalStateException("Cannot found a free cell on map " + map.id());
    }

    /**
     * Get stream of groups related to the current group position
     */
    private Stream<MonsterGroup> groupStream() {
        return map.creatures().stream()
            .filter(MonsterGroup.class::isInstance)
            .map(creature -> (MonsterGroup) creature)
        ;
    }
}
