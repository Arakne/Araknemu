package fr.quatrevieux.araknemu.game.monster.group;

import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.game.monster.group.generator.MonsterListGenerator;
import fr.quatrevieux.araknemu.game.world.map.Direction;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Creates monster groups
 */
final public class MonsterGroupFactory {
    final private MonsterListGenerator generator;

    /**
     * Last group id for generate id sequence
     */
    final private AtomicInteger lastGroupId = new AtomicInteger();

    public MonsterGroupFactory(MonsterListGenerator generator) {
        this.generator = generator;
    }

    /**
     * Create the monster group from data
     */
    public MonsterGroup create(MonsterGroupData data, int cell) {
        return new MonsterGroup(
            lastGroupId.incrementAndGet(),
            generator.generate(data),
            Direction.SOUTH_EAST,
            cell
        );
    }
}
