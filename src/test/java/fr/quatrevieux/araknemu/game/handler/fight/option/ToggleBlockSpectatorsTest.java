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
import fr.quatrevieux.araknemu.game.fight.spectator.SpectatorFactory;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.turn.order.AlternateTeamFighterOrder;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.fight.option.BlockSpectatorRequest;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;
import fr.quatrevieux.araknemu.network.game.out.fight.CancelFight;
import fr.quatrevieux.araknemu.network.game.out.fight.FightOption;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// @todo test with unmodifiable team options
class ToggleBlockSpectatorsTest extends FightBaseCase {
    @RepeatedIfExceptionsTest
    void shouldSendInformationMessageOnToggle() throws Exception {
        Fight fight = createFight();

        handlePacket(new BlockSpectatorRequest());
        requestStack.assertLast(Information.spectatorsBlocked());
        assertFalse(fight.team(0).options().allowSpectators());

        handlePacket(new BlockSpectatorRequest());
        requestStack.assertLast(Information.spectatorsAllowed());
        assertTrue(fight.team(0).options().allowSpectators());
    }

    @RepeatedIfExceptionsTest
    void shouldIgnoreIfNotTeamLeader() throws Exception {
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));
        fight.state(PlacementState.class).joinTeam(makePlayerFighter(gamePlayer()), fight.team(0));

        handlePacket(new BlockSpectatorRequest());

        requestStack.assertLast(new Noop());
        assertTrue(fight.team(0).options().allowSpectators());
    }

    @Test
    void notInFightShouldFail() {
        assertThrows(CloseImmediately.class, () -> handlePacket(new BlockSpectatorRequest()));
    }

    @Test
    void fromExplorationMapShouldSendTeamOptionAndDenyJoin() throws Exception {
        explorationPlayer().changeMap(container.get(ExplorationMapService.class).load(10340), 150);
        ToggleBlockSpectator handler = new ToggleBlockSpectator();
        Fight fight = createSimpleFight(explorationPlayer().map());
        PlayerFighter leader = (PlayerFighter) fight.team(0).leader();

        Field f = GamePlayer.class.getDeclaredField("session");
        f.setAccessible(true);
        GameSession leaderSession = (GameSession) f.get(leader.player());
        leaderSession.setFighter(leader);

        handler.handle(leaderSession, new BlockSpectatorRequest());
        requestStack.assertLast(new FightOption(leader.team().id(), FightOption.Type.BLOCK_SPECTATOR, true));

        handler.handle(leaderSession, new BlockSpectatorRequest());
        requestStack.assertLast(new FightOption(leader.team().id(), FightOption.Type.BLOCK_SPECTATOR, false));
    }

    @Test
    void shouldKickSpectators() throws Exception {
        ToggleBlockSpectator handler = new ToggleBlockSpectator();
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));
        fight.start(new AlternateTeamFighterOrder());
        PlayerFighter leader = (PlayerFighter) fight.team(0).leader();

        Field f = GamePlayer.class.getDeclaredField("session");
        f.setAccessible(true);
        GameSession leaderSession = (GameSession) f.get(leader.player());
        leaderSession.setFighter(leader);

        container.get(SpectatorFactory.class).create(gamePlayer(), fight).join();

        handler.handle(leaderSession, new BlockSpectatorRequest());
        requestStack.assertLast(new CancelFight());
        assertFalse(gamePlayer().isSpectator());
    }
}
