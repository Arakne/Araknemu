package fr.quatrevieux.araknemu.game.listener.map.fight;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.event.FightCreated;
import fr.quatrevieux.araknemu.game.fight.event.FightStopped;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.AddTeamFighters;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.FightsCount;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.ShowFight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class SendCreatedFightTest extends FightBaseCase {
    private SendCreatedFight listener;
    private ExplorationMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendCreatedFight(
            container.get(ExplorationMapService.class),
            container.get(FightService.class)
        );

        map = container.get(ExplorationMapService.class).load(10340);

        explorationPlayer().join(map);
    }

    @Test
    void onFightCreated() throws SQLException, ContainerException {
        createSimpleFight(map);

        Fight fight = createSimpleFight(map);
        requestStack.clear();

        listener.on(new FightCreated(fight));

        requestStack.assertAll(
            new ShowFight(fight),
            new AddTeamFighters(fight.team(0)),
            new AddTeamFighters(fight.team(1)),
            new FightsCount(2)
        );
    }
}
