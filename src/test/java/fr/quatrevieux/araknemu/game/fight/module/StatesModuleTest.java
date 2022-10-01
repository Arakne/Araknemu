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

package fr.quatrevieux.araknemu.game.fight.module;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.order.AlternateTeamFighterOrder;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.RefreshStates;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.SendState;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatesModuleTest extends FightBaseCase {
    @Test
    void listeners() throws Exception {
        Fight fight = createFight(false);

        fight.register(new RaulebaqueModule(fight));
        fight.nextState();
        fight.start(new AlternateTeamFighterOrder());

        assertTrue(fight.dispatcher().has(RefreshStates.class));
        assertTrue(fight.dispatcher().has(SendState.class));

        fight.stop();

        assertFalse(fight.dispatcher().has(RefreshStates.class));
        assertFalse(fight.dispatcher().has(SendState.class));
    }

    @Test
    void pushStateEffect() throws Exception {
        Fight fight = createFight(false);

        fight.register(new RaulebaqueModule(fight));
        fight.nextState();
        fight.start(new AlternateTeamFighterOrder());

        FightCastScope scope = makeCastScopeForEffect(950);

        fight.effects().apply(scope);

        requestStack.assertLast(ActionEffect.addState(other.fighter(), 0));
    }

    @Test
    void removeStateEffect() throws Exception {
        Fight fight = createFight(false);

        fight.register(new RaulebaqueModule(fight));
        fight.nextState();
        fight.start(new AlternateTeamFighterOrder());

        other.fighter().states().push(0);
        requestStack.clear();

        FightCastScope scope = makeCastScopeForEffect(951);

        fight.effects().apply(scope);

        requestStack.assertLast(ActionEffect.removeState(other.fighter(), 0));
    }
}
