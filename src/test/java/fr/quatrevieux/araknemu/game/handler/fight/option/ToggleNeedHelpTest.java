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
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.fight.option.NeedHelpRequest;
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
class ToggleNeedHelpTest extends FightBaseCase {
    @RepeatedIfExceptionsTest
    void shouldSendInformationMessageOnToggle() throws Exception {
        Fight fight = createFight();

        handlePacket(new NeedHelpRequest());
        requestStack.assertLast(Information.helpRequested());
        assertTrue(fight.team(0).options().needHelp());

        handlePacket(new NeedHelpRequest());
        requestStack.assertLast(Information.helpRequestStopped());
        assertFalse(fight.team(0).options().needHelp());
    }

    @Test
    void shouldIgnoreIfNotTeamLeader() throws Exception {
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));
        fight.state(PlacementState.class).joinTeam(makePlayerFighter(gamePlayer()), fight.team(0));

        handlePacket(new NeedHelpRequest());

        requestStack.assertLast(new Noop());
        assertFalse(fight.team(0).options().needHelp());
    }

    @Test
    void shouldIgnoreIfActiveFight() throws Exception {
        Fight fight = createFight();
        fight.nextState();

        handlePacket(new NeedHelpRequest());

        requestStack.assertLast(new Noop());
        assertFalse(fight.team(0).options().needHelp());
    }

    @Test
    void notInFightShouldFail() {
        assertThrows(CloseImmediately.class, () -> handlePacket(new NeedHelpRequest()));
    }

    @Test
    void fromExplorationMapShouldSendTeamOption() throws Exception {
        explorationPlayer().changeMap(container.get(ExplorationMapService.class).load(10340), 150);
        ToggleNeedHelp handler = new ToggleNeedHelp();
        Fight fight = createSimpleFight(explorationPlayer().map());
        PlayerFighter leader = (PlayerFighter) fight.team(0).leader();

        Field f = GamePlayer.class.getDeclaredField("session");
        f.setAccessible(true);
        GameSession leaderSession = (GameSession) f.get(leader.player());
        leaderSession.setFighter(leader);

        handler.handle(leaderSession, new NeedHelpRequest());
        requestStack.assertLast(new FightOption(leader.team().id(), FightOption.Type.NEED_HELP, true));

        handler.handle(leaderSession, new NeedHelpRequest());
        requestStack.assertLast(new FightOption(leader.team().id(), FightOption.Type.NEED_HELP, false));
    }
}
