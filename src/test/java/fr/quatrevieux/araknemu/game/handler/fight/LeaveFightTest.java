package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.exception.JoinFightException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.in.fight.LeaveFightRequest;
import fr.quatrevieux.araknemu.network.game.out.fight.CancelFight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class LeaveFightTest extends FightBaseCase {
    private LeaveFight handler;
    private ExplorationMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        map = container.get(ExplorationMapService.class).load(10340);

        handler = new LeaveFight();
        explorationPlayer();
    }

    @Test
    void leaveFightDuringPlacementNotLeader() throws SQLException, ContainerException, JoinFightException {
        Fight fight = createSimpleFight(map);
        Fighter fighter = new PlayerFighter(gamePlayer());

        fight.state(PlacementState.class).joinTeam(fighter, fight.team(0));
        requestStack.clear();

        handler.handle(session, new LeaveFightRequest());

        assertFalse(gamePlayer().isFighting());
        assertFalse(fight.fighters().contains(fighter));
        assertCount(2, fight.fighters());
        requestStack.assertLast(new CancelFight());
    }

    @Test
    void leaveFightDuringPlacementLeaderWillDissolveTeam() throws Exception {
        Fight fight = createFight();

        GamePlayer other = makeSimpleGamePlayer(10);
        PlayerFighter otherFighter = new PlayerFighter(other);

        fight.state(PlacementState.class).joinTeam(otherFighter, fight.team(0));
        requestStack.clear();

        handler.handle(session, new LeaveFightRequest());

        assertFalse(gamePlayer().isFighting());
        assertFalse(other.isFighting());
        assertCount(0, fight.fighters());
        assertTrue(container.get(FightService.class).fightsByMap(map.id()).isEmpty());

        requestStack.assertLast(new CancelFight());
    }
}
