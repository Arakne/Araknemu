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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.handler.fight.option;

import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.exception.JoinFightException;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.fight.option.LockTeamRequest;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;
import fr.quatrevieux.araknemu.network.game.out.fight.FightOption;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// @todo test with unmodifiable team options
class ToggleLockTeamTest extends FightBaseCase {
    @RepeatedIfExceptionsTest
    void shouldSendInformationMessageOnToggle() throws Exception {
        Fight fight = createFight();

        handlePacket(new LockTeamRequest());
        requestStack.assertLast(Information.joinTeamLocked());
        assertFalse(fight.team(0).options().allowJoinTeam());

        handlePacket(new LockTeamRequest());
        requestStack.assertLast(Information.joinTeamReleased());
        assertTrue(fight.team(0).options().allowJoinTeam());
    }

    @Test
    void shouldIgnoreIfNotTeamLeader() throws Exception {
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));
        fight.state(PlacementState.class).joinTeam(makePlayerFighter(gamePlayer()), fight.team(0));

        handlePacket(new LockTeamRequest());

        requestStack.assertLast(new Noop());
        assertTrue(fight.team(0).options().allowJoinTeam());
    }

    @Test
    void shouldIgnoreIfActiveFight() throws Exception {
        Fight fight = createFight();
        fight.nextState();

        handlePacket(new LockTeamRequest());

        requestStack.assertLast(new Noop());
        assertTrue(fight.team(0).options().allowJoinTeam());
    }

    @Test
    void notInFightShouldFail() {
        assertThrows(CloseImmediately.class, () -> handlePacket(new LockTeamRequest()));
    }

    @Test
    void fromExplorationMapShouldSendTeamOptionAndDenyJoin() throws Exception {
        explorationPlayer().changeMap(container.get(ExplorationMapService.class).load(10340), 150);
        ToggleLockTeam handler = new ToggleLockTeam();
        Fight fight = createSimpleFight(explorationPlayer().map());
        PlayerFighter leader = (PlayerFighter) fight.team(0).leader();

        Field f = GamePlayer.class.getDeclaredField("session");
        f.setAccessible(true);
        GameSession leaderSession = (GameSession) f.get(leader.player());
        leaderSession.setFighter(leader);

        handler.handle(leaderSession, new LockTeamRequest());
        requestStack.assertLast(new FightOption(leader.team().id(), FightOption.Type.BLOCK_JOINER, true));
        assertThrows(JoinFightException.class, () -> fight.state(PlacementState.class).joinTeam(makePlayerFighter(gamePlayer()), fight.team(0)));

        handler.handle(leaderSession, new LockTeamRequest());
        requestStack.assertLast(new FightOption(leader.team().id(), FightOption.Type.BLOCK_JOINER, false));
        PlayerFighter fighter = makePlayerFighter(gamePlayer());
        fight.state(PlacementState.class).joinTeam(fighter, fight.team(0));
        assertContains(fighter, fight.fighters());
    }
}
