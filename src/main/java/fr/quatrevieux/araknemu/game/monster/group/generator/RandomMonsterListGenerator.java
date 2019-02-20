package fr.quatrevieux.araknemu.game.monster.group.generator;

import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.game.monster.Monster;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.util.RandomUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Generate a random monster group
 *
 * - A random group size will be choose
 * - For each monster, a random one is choose from group data
 */
final public class RandomMonsterListGenerator implements MonsterListGenerator {
    final private MonsterService service;
    final private RandomUtil random;

    final static private int[][] SIZE_PROBABILITIES = new int[][] {
        {50, 50},
        {33, 34, 33},
        {22, 26, 26, 26},
        {15, 20, 25, 25, 15},
        {10, 15, 20, 20, 20, 15},
        {9, 11, 15, 20, 20, 16, 9},
        {9, 11, 13, 17, 17, 13, 11, 9},
    };

    public RandomMonsterListGenerator(MonsterService service) {
        this.service = service;
        this.random = new RandomUtil();
    }

    @Override
    public List<Monster> generate(MonsterGroupData data) {
        final int size = groupSize(data.maxSize());

        List<Monster> monsters = new ArrayList<>(size);

        for (int i = size; i > 0; --i) {
            MonsterGroupData.Monster monsterData = random.of(data.monsters());

            monsters.add(service.load(monsterData.id()).random(monsterData.level()));
        }

        return monsters;
    }

    /**
     * Get a random group size from the maximal size
     */
    private int groupSize(int maxSize) {
        if (maxSize == 1) {
            return 1;
        }

        // size - 2 because size 0 do not exists, and size 1 has no randomization
        final int[] probabilities = SIZE_PROBABILITIES[maxSize - 2];

        int dice = random.rand(1, 100);

        for (int size = 1; size <= probabilities.length; ++size) {
            dice -= probabilities[size - 1];

            if (dice <= 0) {
                return size;
            }
        }

        throw new IllegalArgumentException("Invalid group size " + maxSize);
    }
}
