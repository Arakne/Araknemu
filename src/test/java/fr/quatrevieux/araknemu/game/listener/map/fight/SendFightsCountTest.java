package fr.quatrevieux.araknemu.game.listener.map.fight;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.event.FightStarted;
import fr.quatrevieux.araknemu.game.fight.event.FightStopped;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.FightsCount;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.HideFight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class SendFightsCountTest extends FightBaseCase {
    private SendFightsCount listener;
    private ExplorationMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendFightsCount(
            map = container.get(ExplorationMapService.class).load(10340),
            container.get(FightService.class)
        );

        explorationPlayer().join(map);
    }

    @Test
    void onFightStopped() throws SQLException, ContainerException {
        createSimpleFight(map);

        Fight fight = createSimpleFight(map);
        fight.start();
        fight.stop();
        requestStack.clear();

        listener.on(new FightStopped(fight));

        requestStack.assertLast(new FightsCount(1));
    }
}
