package fr.quatrevieux.araknemu.game.monster.group;

import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterGroupDataRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.monster.group.generator.RandomMonsterListGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MonsterGroupFactoryTest extends GameBaseCase {
    private MonsterGroupFactory factory;
    private MonsterGroupDataRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMonsterTemplates()
            .pushMonsterSpells()
            .pushMonsterGroups()
        ;

        factory = new MonsterGroupFactory(
            new RandomMonsterListGenerator(
                container.get(MonsterService.class)
            )
        );

        repository = container.get(MonsterGroupDataRepository.class);
    }

    @Test
    void create() {
        MonsterGroup group = factory.create(repository.get(1), 123);

        assertEquals(-103, group.id());
        assertEquals(123, group.cell());
        assertBetween(1, 4, group.monsters().size());
    }

    @Test
    void createIdIncrement() {
        assertEquals(-103, factory.create(repository.get(1), 123).id());
        assertEquals(-203, factory.create(repository.get(1), 123).id());
        assertEquals(-303, factory.create(repository.get(1), 123).id());
        assertEquals(-403, factory.create(repository.get(1), 123).id());
    }
}
