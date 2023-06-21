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

package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.fight.KickFighterRequest;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;
import fr.quatrevieux.araknemu.network.game.out.fight.CancelFight;
import fr.quatrevieux.araknemu.network.game.out.game.RemoveSprite;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KickFighterTest extends FightBaseCase {
    @Test
    void notInFight() {
        assertThrows(CloseImmediately.class, () -> handlePacket(new KickFighterRequest(12)));
    }

    @RepeatedIfExceptionsTest
    void notInPlacementState() throws Exception {
        Fight fight = createFight();
        Fighter teammate = makePlayerFighter(makeSimpleGamePlayer(10));
        fight.state(PlacementState.class).joinTeam(teammate, gamePlayer().fighter().team());
        fight.nextState();

        requestStack.clear();
        handlePacket(new KickFighterRequest(teammate.id()));

        assertTrue(fight.fighters().all().contains(teammate));
        requestStack.assertLast(Error.cantDoDuringFight());
    }

    @RepeatedIfExceptionsTest
    void notTeammate() throws Exception {
        Fight fight = createFight();
        Fighter enemy = makePlayerFighter(makeSimpleGamePlayer(10));
        fight.state(PlacementState.class).joinTeam(enemy, fight.team(1));

        handlePacket(new KickFighterRequest(enemy.id()));

        assertTrue(fight.fighters().all().contains(enemy));
        requestStack.assertLast(new Noop());
    }

    @RepeatedIfExceptionsTest
    void fighterNotFound() throws Exception {
        Fight fight = createFight();

        handlePacket(new KickFighterRequest(404));

        requestStack.assertLast(new Noop());
    }

    @RepeatedIfExceptionsTest
    void notLeader() throws Exception {
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));
        Fighter me = makePlayerFighter(gamePlayer());

        fight.state(PlacementState.class).joinTeam(me, fight.team(0));

        handlePacket(new KickFighterRequest(me.id()));

        requestStack.assertLast(new Noop());
        assertTrue(fight.fighters().all().contains(me));
    }

    @RepeatedIfExceptionsTest
    void success() throws Exception {
        Fight fight = createFight();
        Fighter teammate = makePlayerFighter(makeSimpleGamePlayer(10));
        fight.state(PlacementState.class).joinTeam(teammate, gamePlayer().fighter().team());

        handlePacket(new KickFighterRequest(teammate.id()));

        assertFalse(fight.fighters().all().contains(teammate));
        requestStack.assertLast(new RemoveSprite(teammate.sprite()));
    }

    @Test
    void otherKickMe() throws Exception {
        Fight fight = createSimpleFight(container.get(ExplorationMapService.class).load(10340));
        Fighter me = makePlayerFighter(gamePlayer());

        fight.state(PlacementState.class).joinTeam(me, fight.team(0));

        PlayerFighter otherFighter = (PlayerFighter) fight.team(0).leader();
        GameSession otherSession = accessors.session(otherFighter);
        otherSession.setFighter(otherFighter);
        new KickFighter().handle(otherSession, new KickFighterRequest(me.id()));

        requestStack.assertLast(new CancelFight());
        assertFalse(fight.fighters().all().contains(me));
    }
}
