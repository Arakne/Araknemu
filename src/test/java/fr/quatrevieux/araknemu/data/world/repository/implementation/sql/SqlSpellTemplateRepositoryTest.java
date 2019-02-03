package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.world.entity.SpellTemplate;
import fr.quatrevieux.araknemu.data.world.transformer.SpellTemplateLevelTransformer;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlSpellTemplateRepositoryTest extends GameBaseCase {
    private SqlSpellTemplateRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushSpells();

        repository = new SqlSpellTemplateRepository(
            app.database().get("game"),
            container.get(SpellTemplateLevelTransformer.class)
        );
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(-5));
    }

    @Test
    void getById() {
        SpellTemplate spell = repository.get(2);

        assertEquals(2, spell.id());
        assertEquals("Aveuglement", spell.name());
        assertEquals(102, spell.sprite());
        assertEquals("11,1,1", spell.spriteArgs());
        assertCount(6, spell.levels());
        assertArrayEquals(new int[0], spell.targets());
    }

    @Test
    void getByTemplate() {
        SpellTemplate spell = repository.get(new SpellTemplate(2, null, 0, null, null, null));

        assertEquals(2, spell.id());
        assertEquals("Aveuglement", spell.name());
        assertEquals(102, spell.sprite());
        assertEquals("11,1,1", spell.spriteArgs());
        assertCount(6, spell.levels());
        assertArrayEquals(new int[0], spell.targets());
    }

    @Test
    void has() {
        assertTrue(repository.has(new SpellTemplate(3, null, 0, null, null, null)));
        assertFalse(repository.has(new SpellTemplate(-3, null, 0, null, null, null)));
    }

    @Test
    void load() {
        assertCount(5, repository.load());
    }
}