package fr.quatrevieux.araknemu.game.monster.group.generator;

import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.game.monster.Monster;
import fr.quatrevieux.araknemu.game.monster.MonsterService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Generate a fixed monster group
 * The only randomization is the monster level
 * All monsters of the data will be generated
 */
final public class FixedMonsterListGenerator implements MonsterListGenerator {
    final private MonsterService service;

    public FixedMonsterListGenerator(MonsterService service) {
        this.service = service;
    }

    @Override
    public List<Monster> generate(MonsterGroupData data) {
        return data.monsters().stream()
            .map(monster -> service.load(monster.id()).random(monster.level()))
            .collect(Collectors.toList())
        ;
    }
}
