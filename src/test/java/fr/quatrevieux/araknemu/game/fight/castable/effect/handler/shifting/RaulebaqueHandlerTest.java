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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.exception.JoinFightException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.module.RaulebaqueModule;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterPositions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RaulebaqueHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private RaulebaqueHandler handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        RaulebaqueModule module = new RaulebaqueModule(fight);
        fight.register(module);

        caster = player.fighter();

        caster.move(fight.map().get(123));
        other.fighter().move(fight.map().get(321));

        fight.nextState();

        handler = new RaulebaqueHandler(fight, module);

        requestStack.clear();
    }

    @Test
    void handleSimple() {
        caster.move(fight.map().get(125));
        other.fighter().move(fight.map().get(325));

        FightCastScope scope = makeCastScopeForEffect(784);

        handler.handle(scope, scope.effects().get(0));

        requestStack.assertLast(new FighterPositions(fight.fighters()));

        assertEquals(123, caster.cell().id());
        assertEquals(321, other.fighter().cell().id());
    }

    @Test
    void handleWillNotTeleportAddedFighters() throws SQLException, ContainerException, JoinFightException {
        caster.move(fight.map().get(125));
        other.fighter().move(fight.map().get(325));

        Fighter newFighter = makePlayerFighter(makeSimpleGamePlayer(10));
        newFighter.move(fight.map().get(235));
        fight.team(0).join(newFighter);

        newFighter.move(fight.map().get(236));

        FightCastScope scope = makeCastScopeForEffect(784);

        handler.handle(scope, scope.effects().get(0));

        requestStack.assertLast(new FighterPositions(fight.fighters()));

        assertEquals(123, caster.cell().id());
        assertEquals(321, other.fighter().cell().id());
        assertEquals(236, newFighter.cell().id());
    }

    @Test
    void handleWillExchangePlaceIfTargetIsNotAvailable() throws SQLException, ContainerException, JoinFightException {
        caster.move(fight.map().get(125));
        other.fighter().move(fight.map().get(325));

        Fighter newFighter = makePlayerFighter(makeSimpleGamePlayer(10));
        newFighter.move(fight.map().get(123));
        fight.team(0).join(newFighter);

        FightCastScope scope = makeCastScopeForEffect(784);

        handler.handle(scope, scope.effects().get(0));

        requestStack.assertLast(new FighterPositions(fight.fighters()));

        assertEquals(123, caster.cell().id());
        assertEquals(321, other.fighter().cell().id());
        assertEquals(125, newFighter.cell().id());
    }

    @Test
    void handleWithoutChanges() {
        FightCastScope scope = makeCastScopeForEffect(784);

        handler.handle(scope, scope.effects().get(0));

        requestStack.assertLast(new FighterPositions(fight.fighters()));

        assertEquals(123, caster.cell().id());
        assertEquals(321, other.fighter().cell().id());
    }

    @Test
    void handleWillNotMoveDeadFighter() {
        FightCastScope scope = makeCastScopeForEffect(784);

        other.fighter().move(fight.map().get(124));
        other.fighter().life().kill(other.fighter());

        handler.handle(scope, scope.effects().get(0));

        requestStack.assertLast(new FighterPositions(fight.fighters()));

        assertEquals(123, caster.cell().id());
        assertThrows(IllegalStateException.class, other.fighter()::cell);
        assertFalse(fight.map().get(124).hasFighter());
    }

    @Test
    void buff() {
        FightCastScope scope = makeCastScopeForEffect(784);

        assertThrows(UnsupportedOperationException.class, () -> handler.buff(scope, scope.effects().get(0)));
    }
}
