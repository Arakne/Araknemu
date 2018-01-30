package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Alignment;
import fr.quatrevieux.araknemu.data.living.entity.environment.SubArea;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class SubAreaRepositoryTest extends GameBaseCase {
    private SubAreaRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.use(SubArea.class);
        repository = new SubAreaRepository(app.database().get("game"));
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(new SubArea(45, 0, "", false, null)));
    }

    @Test
    void getFound() throws SQLException, ContainerException {
        dataSet.pushSubArea(new SubArea(4, 0, "La forêt d'Amakna", true, Alignment.NONE));

        SubArea area = repository.get(new SubArea(4, 0, null, false, null));

        assertEquals(4, area.id());
        assertEquals(0, area.area());
        assertEquals("La forêt d'Amakna", area.name());
        assertEquals(true, area.conquestable());
        assertEquals(Alignment.NONE, area.alignment());
    }

    @Test
    void has() throws SQLException, ContainerException {
        SubArea area = new SubArea(4, 0, "La forêt d'Amakna", true, Alignment.NONE);

        assertFalse(repository.has(area));

        dataSet.pushSubArea(area);

        assertTrue(repository.has(area));
    }

    @Test
    void all() throws SQLException, ContainerException {
        dataSet.pushSubArea(new SubArea(1, 0, "Port de Madrestam", true, Alignment.NONE));
        dataSet.pushSubArea(new SubArea(2, 0, "La montagne des Craqueleurs", true, Alignment.NEUTRAL));
        dataSet.pushSubArea(new SubArea(3, 0, "Le champ des Ingalsses", true, Alignment.BONTARIAN));
        dataSet.pushSubArea(new SubArea(4, 0, "La forêt d'Amakna", true, Alignment.BRAKMARIAN));

        Collection<SubArea> areas = repository.all();

        assertCount(4, areas);
        assertConstainsOnly(SubArea.class, areas.toArray());
    }
}
