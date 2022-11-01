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
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.order.AlternateTeamFighterOrder;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 */
class RaulebaqueModuleTest extends FightBaseCase {
    @Test
    void effect() throws Exception {
        Fight fight = createFight(false);

        fight.register(new RaulebaqueModule(fight));
        fight.nextState();
        fight.start(new AlternateTeamFighterOrder());

        FightCell playerCell = player.fighter().cell();
        FightCell otherCell = other.fighter().cell();

        player.fighter().move(fight.map().get(123));
        other.fighter().move(fight.map().get(321));

        FightCastScope scope = makeCastScopeForEffect(784);

        fight.effects().apply(scope);

        assertSame(playerCell, player.fighter().cell());
        assertSame(otherCell, other.fighter().cell());
    }

    @Test
    void startPositions() throws Exception {
        Fight fight = createFight(false);
        RaulebaqueModule module = new RaulebaqueModule(fight);

        assertThrows(IllegalStateException.class, () -> module.startPositions());

        fight.register(module);
        fight.nextState();
        fight.start(new AlternateTeamFighterOrder());

        FightCell playerCell = player.fighter().cell();
        FightCell otherCell = other.fighter().cell();

        Map<Fighter, FightCell> expected = new HashMap<>();
        expected.put(player.fighter(), playerCell);
        expected.put(other.fighter(), otherCell);

        assertEquals(expected, module.startPositions());
    }
}
