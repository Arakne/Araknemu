package fr.quatrevieux.araknemu.network.game.out.fight.exploration;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class HideFightTest extends FightBaseCase {
    @Test
    void generate() throws ContainerException, SQLException {
        dataSet.pushMaps();

        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));

        assertEquals("Gc-1", new HideFight(fight).toString());
    }
}