package fr.quatrevieux.araknemu.game.event.listener.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.exploration.MapLoaded;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.network.game.out.game.MapData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SendMapDataTest extends GameBaseCase {
    private SendMapData listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        listener = new SendMapData(
            explorationPlayer()
        );
    }

    @Test
    void onMapLoaded() throws ContainerException {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10300);

        listener.on(
            new MapLoaded(map)
        );

        requestStack.assertLast(
            new MapData(map)
        );
    }
}
