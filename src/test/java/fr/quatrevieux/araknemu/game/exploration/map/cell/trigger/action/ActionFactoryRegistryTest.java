package fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.teleport.Teleport;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.teleport.TeleportFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ActionFactoryRegistryTest extends GameBaseCase {
    private ActionFactoryRegistry registry;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        registry = new ActionFactoryRegistry();
    }

    @Test
    void createActionNotFound() {
        assertThrows(NoSuchElementException.class, () -> registry.create(new MapTrigger(10300, 123, 15, "", "")));
    }

    @Test
    void registerAndCreate() throws ContainerException {
        assertSame(registry, registry.register(0, new TeleportFactory(container.get(ExplorationMapService.class))));

        assertInstanceOf(Teleport.class, registry.create(new MapTrigger(10300, 123, 0, "10340,456", "-1")));
    }
}