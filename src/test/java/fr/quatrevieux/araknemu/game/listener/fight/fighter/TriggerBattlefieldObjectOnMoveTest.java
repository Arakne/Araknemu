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
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterMoved;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldObject;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TriggerBattlefieldObjectOnMoveTest extends FightBaseCase {
    private Fight fight;
    private TriggerBattlefieldObjectOnMove listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();
        fight.turnList().start();
        listener = new TriggerBattlefieldObjectOnMove(fight);

        fight.dispatcher().remove(RemoveBattlefieldObjects.class);
        requestStack.clear();
    }

    @Test
    void onMove() {
        BattlefieldObject bo1 = Mockito.mock(BattlefieldObject.class);
        BattlefieldObject bo2 = Mockito.mock(BattlefieldObject.class);

        Mockito.when(bo1.caster()).thenReturn(player.fighter());
        Mockito.when(bo2.caster()).thenReturn(player.fighter());

        Mockito.when(bo1.cell()).thenReturn(fight.map().get(137));
        Mockito.when(bo2.cell()).thenReturn(fight.map().get(153));

        Mockito.when(bo1.size()).thenReturn(1);
        Mockito.when(bo2.size()).thenReturn(1);

        Mockito.when(bo1.isOnArea(Mockito.any(Fighter.class))).thenCallRealMethod();
        Mockito.when(bo2.isOnArea(Mockito.any(Fighter.class))).thenCallRealMethod();
        Mockito.when(bo1.isOnArea(Mockito.any(FightCell.class))).thenCallRealMethod();
        Mockito.when(bo2.isOnArea(Mockito.any(FightCell.class))).thenCallRealMethod();

        fight.map().objects().add(bo1);
        fight.map().objects().add(bo2);

        player.fighter().move(fight.map().get(123));
        listener.on(new FighterMoved(player.fighter(), fight.map().get(123)));

        Mockito.verify(bo1).onEnterInArea(player.fighter());
        Mockito.verify(bo2, Mockito.never()).onEnterInArea(player.fighter());
    }
}
