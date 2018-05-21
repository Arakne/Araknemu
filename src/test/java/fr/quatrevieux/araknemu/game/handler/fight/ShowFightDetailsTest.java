package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.network.game.in.fight.AskFightDetails;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.FightDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class ShowFightDetailsTest extends FightBaseCase {
    private ShowFightDetails handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new ShowFightDetails(container.get(FightService.class));
    }

    @Test
    void handleSuccess() throws SQLException, ContainerException {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);
        explorationPlayer().join(map);

        Fight fight = createSimpleFight(map);

        handler.handle(session, new AskFightDetails(fight.id()));

        requestStack.assertLast(new FightDetails(fight));
    }
}