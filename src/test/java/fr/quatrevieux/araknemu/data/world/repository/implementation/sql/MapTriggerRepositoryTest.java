package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.trigger.CellAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class MapTriggerRepositoryTest extends GameBaseCase {
    private MapTriggerRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        repository = new MapTriggerRepository(
            app.database().get("game")
        );

        dataSet.use(MapTrigger.class);
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(new MapTrigger(789, 741, 456, null, null, null)));
    }

    @Test
    void getFound() throws SQLException, ContainerException {
        MapTrigger trigger = dataSet.pushTrigger(new MapTrigger(1, 456, 123, CellAction.TELEPORT, "741,258", ""));

        MapTrigger db = repository.get(trigger);

        assertEquals(1, db.id());
        assertEquals(456, db.map());
        assertEquals(123, db.cell());
        assertEquals(CellAction.TELEPORT, db.action());
        assertEquals("741,258", db.arguments());
    }

    @Test
    void has() throws SQLException, ContainerException {
        MapTrigger trigger = dataSet.pushTrigger(new MapTrigger(1, 456, 123, CellAction.TELEPORT, "741,258", ""));

        assertTrue(repository.has(trigger));
        assertFalse(repository.has(new MapTrigger(-1, 1, 2, null, null, null)));
    }

    @Test
    void findByMap() throws SQLException, ContainerException {
        dataSet.pushTrigger(new MapTrigger(1, 456, 123, CellAction.TELEPORT, "741,258", ""));
        dataSet.pushTrigger(new MapTrigger(2, 457, 123, CellAction.TELEPORT, "741,258", ""));
        dataSet.pushTrigger(new MapTrigger(3, 456, 124, CellAction.TELEPORT, "741,258", ""));

        Collection<MapTrigger> triggers = repository.findByMap(new MapTemplate(456, null, null, null, null));

        assertCount(2, triggers);
    }

    @Test
    void all() throws SQLException, ContainerException {
        dataSet.pushTrigger(new MapTrigger(1, 456, 123, CellAction.TELEPORT, "741,258", ""));
        dataSet.pushTrigger(new MapTrigger(2, 457, 123, CellAction.TELEPORT, "741,258", ""));
        dataSet.pushTrigger(new MapTrigger(3, 456, 124, CellAction.TELEPORT, "741,258", ""));

        Collection<MapTrigger> triggers = repository.all();

        assertCount(3, triggers);
    }
}
