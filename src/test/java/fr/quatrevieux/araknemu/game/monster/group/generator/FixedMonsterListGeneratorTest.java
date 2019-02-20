package fr.quatrevieux.araknemu.game.monster.group.generator;

import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterGroupDataRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.monster.Monster;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FixedMonsterListGeneratorTest extends GameBaseCase {
    private FixedMonsterListGenerator generator;
    private MonsterGroupDataRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMonsterTemplates()
            .pushMonsterGroups()
            .pushMonsterSpells()
        ;

        repository = container.get(MonsterGroupDataRepository.class);
        generator = new FixedMonsterListGenerator(container.get(MonsterService.class));
    }

    @Test
    void generate() {
        List<Monster> monsters = generator.generate(repository.get(1));

        assertCount(2, monsters);
        assertArrayEquals(new int[] {31, 34}, monsters.stream().mapToInt(Monster::id).toArray());
        assertBetween(2, 6, monsters.get(0).level());
        assertEquals(10, monsters.get(1).level());
    }
}
