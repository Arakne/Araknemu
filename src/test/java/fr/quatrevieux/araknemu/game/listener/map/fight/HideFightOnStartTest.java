package fr.quatrevieux.araknemu.game.listener.map.fight;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.event.FightStarted;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.HideFight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class HideFightOnStartTest extends FightBaseCase {
    private HideFightOnStart listener;
    private ExplorationMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new HideFightOnStart(
            map = container.get(ExplorationMapService.class).load(10340)
        );

        explorationPlayer().join(map);
    }

    @Test
    void onFightStarted() throws SQLException, ContainerException {
        Fight fight = createSimpleFight(map);
        requestStack.clear();

        listener.on(new FightStarted(fight));

        requestStack.assertLast(new HideFight(fight));
    }
}