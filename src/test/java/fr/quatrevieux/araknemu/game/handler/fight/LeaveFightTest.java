/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.exception.JoinFightException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.state.FinishState;
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

        dataSet.pushMaps().pushSubAreas().pushAreas();

        map = container.get(ExplorationMapService.class).load(10340);

        handler = new LeaveFight();
        explorationPlayer();
    }

    @Test
    void leaveFightDuringPlacementNotLeader() throws SQLException, ContainerException, JoinFightException {
        Fight fight = createSimpleFight(map);
        Fighter fighter = makePlayerFighter(gamePlayer());

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
        PlayerFighter otherFighter = makePlayerFighter(other);

        fight.state(PlacementState.class).joinTeam(otherFighter, fight.team(0));
        requestStack.clear();

        handler.handle(session, new LeaveFightRequest());

        assertFalse(gamePlayer().isFighting());
        assertFalse(other.isFighting());
        assertCount(0, fight.fighters());
        assertTrue(container.get(FightService.class).fightsByMap(map.id()).isEmpty());

        requestStack.assertLast(new CancelFight());
    }

    @Test
    void leaveFightActiveStateNotLastOfTeam() throws SQLException, ContainerException, JoinFightException {
        Fight fight = createSimpleFight(map);
        Fighter fighter = makePlayerFighter(gamePlayer());

        fight.state(PlacementState.class).joinTeam(fighter, fight.team(0));
        fight.state(PlacementState.class).startFight();
        requestStack.clear();

        gamePlayer().setPosition(new Position(10540, 123));

        handler.handle(session, new LeaveFightRequest());

        assertFalse(gamePlayer().isFighting());
        assertFalse(fight.fighters().contains(fighter));
        assertTrue(fight.active());
        assertTrue(fighter.dead());
        assertCount(2, fight.fighters());
        assertCount(2, fight.turnList().fighters());
        assertFalse(fight.turnList().fighters().contains(fighter));
        requestStack.assertLast(new CancelFight());

        assertEquals(new Position(10540, 123), dataSet.refresh(new Player(gamePlayer().id())).position());
    }

    @Test
    void leaveFightActiveStateOnCurrentTurnWillStopTheTurn() throws Exception {
        Fight fight = createFight();

        fight.state(PlacementState.class).joinTeam(makePlayerFighter(makeSimpleGamePlayer(10)), fight.team(0));
        fight.state(PlacementState.class).startFight();
        requestStack.clear();

        assertSame(player.fighter(), fight.turnList().currentFighter());

        handler.handle(session, new LeaveFightRequest());

        assertSame(other.fighter(), fight.turnList().currentFighter());
    }

    @Test
    void leaveFightActiveStateLastOfTeamWillTerminateFight() throws Exception {
        Fight fight = createFight();

        fight.state(PlacementState.class).startFight();
        requestStack.clear();

        handler.handle(session, new LeaveFightRequest());

        assertFalse(fight.active());
    }
}
