package fr.quatrevieux.araknemu.game.world.creature.operation;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class DispatchEventTest extends GameBaseCase {
    @Test
    void onExplorationPlayer() throws SQLException, ContainerException {
        Object event = new Object();
        AtomicReference<Object> ref = new AtomicReference<>();

        explorationPlayer().dispatcher().add(Object.class, ref::set);

        explorationPlayer().apply(new DispatchEvent(event));

        assertSame(event, ref.get());
    }

    @Test
    void onNpc() throws SQLException, ContainerException {
        dataSet
            .pushMaps()
            .pushNpcs()
        ;

        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        GameNpc npc = GameNpc.class.cast(map.creature(-47204));

        npc.apply(new DispatchEvent(new Object()));
    }
}
