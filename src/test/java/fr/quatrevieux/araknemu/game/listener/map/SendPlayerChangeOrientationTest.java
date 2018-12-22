package fr.quatrevieux.araknemu.game.listener.map;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.event.OrientationChanged;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.network.game.out.emote.PlayerOrientation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class SendPlayerChangeOrientationTest extends GameBaseCase {
    private SendPlayerChangeOrientation listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        listener = new SendPlayerChangeOrientation(
            container.get(ExplorationMapService.class).load(10300)
        );
    }

    @Test
    void onOrientationChanged() throws SQLException, ContainerException {
        listener.on(new OrientationChanged(explorationPlayer(), Direction.SOUTH_EAST));

        requestStack.assertLast(new PlayerOrientation(explorationPlayer()));
    }
}
