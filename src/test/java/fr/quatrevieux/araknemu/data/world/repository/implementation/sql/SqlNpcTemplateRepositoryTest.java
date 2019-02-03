package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlNpcTemplateRepositoryTest extends GameBaseCase {
    private SqlNpcTemplateRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushNpcTemplate(new NpcTemplate(23, 9013, 100, 100, Sex.FEMALE, new Colors(8017470, 12288585, 16770534), "0,0,0,0,0", -1, 0));
        dataSet.pushNpcTemplate(new NpcTemplate(40, 9025, 100, 100, Sex.MALE, new Colors(-1, -1, -1), "0,0,0,0,0", -1, 0));
        dataSet.pushNpcTemplate(new NpcTemplate(878, 40, 100, 100, Sex.MALE, new Colors(8158389, 13677665, 3683117), "0,20f9,2a5,1d5e,1b9e", 4, 9092));

        repository = new SqlNpcTemplateRepository(app.database().get("game"));
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(-5));
    }

    @Test
    void getById() {
        NpcTemplate template = repository.get(23);

        assertEquals(23, template.id());
        assertEquals(9013, template.gfxId());
        assertEquals(100, template.scaleX());
        assertEquals(100, template.scaleY());
        assertEquals(Sex.FEMALE, template.sex());
        assertArrayEquals(new Colors(8017470, 12288585, 16770534).toArray(), template.colors().toArray());
        assertEquals("0,0,0,0,0", template.accessories());
        assertEquals(-1, template.extraClip());
        assertEquals(0, template.customArtwork());
    }

    @Test
    void getByEntity() {
        NpcTemplate template = repository.get(new NpcTemplate(23, 0, 0, 0, null, null, null, 0, 0));

        assertEquals(23, template.id());
        assertEquals(9013, template.gfxId());
        assertEquals(100, template.scaleX());
        assertEquals(100, template.scaleY());
        assertEquals(Sex.FEMALE, template.sex());
        assertArrayEquals(new Colors(8017470, 12288585, 16770534).toArray(), template.colors().toArray());
        assertEquals("0,0,0,0,0", template.accessories());
        assertEquals(-1, template.extraClip());
        assertEquals(0, template.customArtwork());
    }

    @Test
    void has() {
        assertTrue(repository.has(new NpcTemplate(23, 0, 0, 0, null, null, null, 0, 0)));
        assertTrue(repository.has(new NpcTemplate(40, 0, 0, 0, null, null, null, 0, 0)));
        assertTrue(repository.has(new NpcTemplate(878, 0, 0, 0, null, null, null, 0, 0)));
        assertFalse(repository.has(new NpcTemplate(-5, 0, 0, 0, null, null, null, 0, 0)));
    }

    @Test
    void all() {
        assertArrayEquals(
            new Object[] {23, 40, 878},
            repository.all().stream().map(NpcTemplate::id).toArray()
        );
    }
}
