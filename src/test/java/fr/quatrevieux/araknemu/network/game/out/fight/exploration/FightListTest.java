package fr.quatrevieux.araknemu.network.game.out.fight.exploration;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.util.DofusDate;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class FightListTest extends FightBaseCase {
    @Test
    void generateNotActive() throws ContainerException, SQLException {
        dataSet.pushMaps();
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        assertEquals(
            "fL1;-1;0,-1,1;0,-1,1|2;-1;0,-1,1;0,-1,1",
            new FightList(Arrays.asList(
                createSimpleFight(map),
                createSimpleFight(map)
            )).toString()
        );
    }

    @Test
    void generateActive() throws SQLException, ContainerException {
        dataSet.pushMaps();
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        Fight fight = createSimpleFight(map);

        fight.start();

        long time = DofusDate.fromDuration(fight.duration()).toMilliseconds();

        String packet = new FightList(Collections.singleton(fight)).toString();

        for (int i = 0; i < 10; ++i) {
            if (packet.equals("fL1;" + (time + i) + ";0,-1,1;0,-1,1")) {
                return;
            }
        }

        fail(packet + " != fL1;" + time + ";0,-1,1;0,-1,1");
    }
}