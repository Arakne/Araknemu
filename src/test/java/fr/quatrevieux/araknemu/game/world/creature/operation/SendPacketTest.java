package fr.quatrevieux.araknemu.game.world.creature.operation;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SendPacketTest extends GameBaseCase {
    @Test
    void onExplorationPlayer() throws SQLException, ContainerException {
        explorationPlayer().apply(new SendPacket("my packet"));

        requestStack.assertLast("my packet");
    }

    @Test
    void onNpc() throws SQLException, ContainerException {
        dataSet
            .pushMaps()
            .pushNpcs()
        ;

        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);
        requestStack.clear();

        GameNpc npc = GameNpc.class.cast(map.creature(-47204));

        npc.apply(new SendPacket("my packet"));

        requestStack.assertEmpty();
    }
}
