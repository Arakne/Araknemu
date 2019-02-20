package fr.quatrevieux.araknemu.game.monster.group.generator;

import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.game.monster.Monster;

import java.util.List;

/**
 * Generate the monster list using randomized or fixed strategy depending of {@link MonsterGroupData#maxSize()} value
 */
final public class MonsterListGeneratorSwitch implements MonsterListGenerator {
    final private MonsterListGenerator randomizedGenerator;
    final private MonsterListGenerator fixedGenerator;

    /**
     * @param randomizedGenerator Generator to used when maxSize > 0
     * @param fixedGenerator Generator to used when maxSize = 0
     */
    public MonsterListGeneratorSwitch(MonsterListGenerator randomizedGenerator, MonsterListGenerator fixedGenerator) {
        this.randomizedGenerator = randomizedGenerator;
        this.fixedGenerator = fixedGenerator;
    }

    @Override
    public List<Monster> generate(MonsterGroupData data) {
        return data.maxSize() > 0
            ? randomizedGenerator.generate(data)
            : fixedGenerator.generate(data)
        ;
    }
}
