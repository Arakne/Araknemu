package fr.quatrevieux.araknemu.game.monster.group.generator;

import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.game.monster.Monster;

import java.util.List;

/**
 * Generate a monster list from a group data
 */
public interface MonsterListGenerator {
    /**
     * Generates the monsters list
     */
    public List<Monster> generate(MonsterGroupData data);
}
