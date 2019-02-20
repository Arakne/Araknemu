package fr.quatrevieux.araknemu.game.monster;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.*;

class MonsterServiceTest extends GameBaseCase {
    private MonsterService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMonsterTemplates()
            .pushMonsterSpells()
        ;

        service = new MonsterService(
            container.get(SpellService.class),
            container.get(MonsterTemplateRepository.class)
        );
    }

    @Test
    void load() {
        GradeSet grades = service.load(31);

        assertCount(5, grades.all());
        assertArrayEquals(
            new int[] {2, 3, 4, 5, 6},
            grades.all().stream().mapToInt(Monster::level).toArray()
        );

        Monster first = grades.all().get(0);

        assertEquals(31, first.id());
        assertEquals(Colors.DEFAULT, first.colors());
        assertEquals(1563, first.gfxId());

        assertTrue(first.spells().has(213));
        assertFalse(first.spells().has(215));
        assertEquals(1, first.spells().get(213).level());
        assertEquals(1, first.spells().get(212).level());

        assertSame(service.load(31), service.load(31));
        assertSame(service.load(34), service.load(34));
        assertNotSame(service.load(31), service.load(34));
    }

    @Test
    void loadNotFound() {
        assertThrows(EntityNotFoundException.class, () -> service.load(404));
    }

    @Test
    void preload() {
        Logger logger = Mockito.mock(Logger.class);

        service.preload(logger);

        Mockito.verify(logger).info("Loading monsters...");
        Mockito.verify(logger).info("{} monsters loaded", 3);
    }
}
