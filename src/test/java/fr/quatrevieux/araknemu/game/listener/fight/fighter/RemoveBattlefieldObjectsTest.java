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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldObject;
import fr.quatrevieux.araknemu.game.listener.fight.CheckFightTerminated;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class RemoveBattlefieldObjectsTest extends FightBaseCase {
    private Fight fight;
    private RemoveBattlefieldObjects listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();
        fight.turnList().start();
        listener = new RemoveBattlefieldObjects(fight);

        fight.dispatcher().remove(CheckFightTerminated.class);
        fight.dispatcher().remove(RemoveDeadFighter.class);
        fight.dispatcher().remove(RemoveBattlefieldObjects.class);
        requestStack.clear();
    }

    @Test
    void onFighterDie() {
        BattlefieldObject bo1 = Mockito.mock(BattlefieldObject.class);
        BattlefieldObject bo2 = Mockito.mock(BattlefieldObject.class);
        BattlefieldObject bo3 = Mockito.mock(BattlefieldObject.class);

        Mockito.when(bo1.caster()).thenReturn(player.fighter());
        Mockito.when(bo2.caster()).thenReturn(other.fighter());
        Mockito.when(bo3.caster()).thenReturn(player.fighter());

        fight.map().addObject(bo1);
        fight.map().addObject(bo2);
        fight.map().addObject(bo3);

        listener.on(new FighterDie(player.fighter(), player.fighter()));

        assertCollectionEquals(fight.map().objects(), bo2);
        Mockito.verify(bo1).disappear();
        Mockito.verify(bo2, Mockito.never()).disappear();
        Mockito.verify(bo3).disappear();
    }
}
