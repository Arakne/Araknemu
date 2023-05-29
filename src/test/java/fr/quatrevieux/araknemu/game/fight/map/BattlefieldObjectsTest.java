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

package fr.quatrevieux.araknemu.game.fight.map;

import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BattlefieldObjectsTest extends GameBaseCase {
    private FightMap map;
    private BattlefieldObjects objects;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        map = new FightMap(
            container.get(MapTemplateRepository.class).get(10340)
        );
        objects = map.objects();
    }

    @Test
    void addObject() {
        assertEquals(0, objects.stream().count());

        BattlefieldObject bo = Mockito.mock(BattlefieldObject.class);
        Mockito.when(bo.cell()).thenReturn(map.get(123));
        Mockito.when(bo.size()).thenReturn(2);

        objects.add(bo);
        assertIterableEquals(objects, Collections.singletonList(bo));
    }

    @Test
    void onStartTurnShouldCallObjectIfFighterInArea() {
        BattlefieldObject bo = Mockito.mock(BattlefieldObject.class);
        Mockito.when(bo.cell()).thenReturn(map.get(123));
        Mockito.when(bo.size()).thenReturn(2);
        Mockito.when(bo.isOnArea(Mockito.any(Fighter.class))).thenCallRealMethod();
        Mockito.when(bo.isOnArea(Mockito.any(FightCell.class))).thenCallRealMethod();
        objects.add(bo);

        Fighter fighter = Mockito.mock(Fighter.class);
        Mockito.when(fighter.cell()).thenReturn(map.get(124));

        objects.onStartTurn(fighter);
        Mockito.verify(bo).onStartTurnInArea(fighter);
    }

    @Test
    void onStartTurnShouldNotCallObjectIfFighterOutOfArea() {
        BattlefieldObject bo = Mockito.mock(BattlefieldObject.class);
        Mockito.when(bo.cell()).thenReturn(map.get(123));
        Mockito.when(bo.size()).thenReturn(2);
        Mockito.when(bo.isOnArea(Mockito.any(Fighter.class))).thenCallRealMethod();
        Mockito.when(bo.isOnArea(Mockito.any(FightCell.class))).thenCallRealMethod();
        objects.add(bo);

        Fighter fighter = Mockito.mock(Fighter.class);
        Mockito.when(fighter.cell()).thenReturn(map.get(170));

        objects.onStartTurn(fighter);
        Mockito.verify(bo, Mockito.never()).onStartTurnInArea(fighter);
    }

    @Test
    void onStartTurnShouldCallRefreshWhenCasterIsGiven() {
        Fighter caster = Mockito.mock(Fighter.class);

        BattlefieldObject bo = Mockito.mock(BattlefieldObject.class);
        Mockito.when(bo.cell()).thenReturn(map.get(123));
        Mockito.when(bo.size()).thenReturn(2);
        Mockito.when(bo.caster()).thenReturn(caster);
        Mockito.when(bo.refresh()).thenReturn(true);
        objects.add(bo);

        objects.onStartTurn(caster);
        Mockito.verify(bo).refresh();

        assertIterableEquals(objects, Collections.singletonList(bo));
    }

    @Test
    void onStartTurnShouldNotCallRefreshWhenOtherFighterIsGiven() {
        Fighter caster = Mockito.mock(Fighter.class);

        BattlefieldObject bo = Mockito.mock(BattlefieldObject.class);
        Mockito.when(bo.cell()).thenReturn(map.get(123));
        Mockito.when(bo.size()).thenReturn(2);
        Mockito.when(bo.caster()).thenReturn(caster);
        Mockito.when(bo.refresh()).thenReturn(true);
        objects.add(bo);

        objects.onStartTurn(Mockito.mock(Fighter.class));
        Mockito.verify(bo, Mockito.never()).refresh();
    }

    @Test
    void onStartTurnShouldRemoveObjectIfRefreshIsFalse() {
        Fighter caster = Mockito.mock(Fighter.class);

        BattlefieldObject bo = Mockito.mock(BattlefieldObject.class);
        Mockito.when(bo.cell()).thenReturn(map.get(123));
        Mockito.when(bo.size()).thenReturn(2);
        Mockito.when(bo.caster()).thenReturn(caster);
        Mockito.when(bo.refresh()).thenReturn(false);
        objects.add(bo);

        objects.onStartTurn(caster);
        Mockito.verify(bo).refresh();
        Mockito.verify(bo).disappear();

        assertEquals(0, objects.stream().count());
    }

    @Test
    void onStartTurnShouldNotCallObjectIfRemoved() {
        Fighter caster = Mockito.mock(Fighter.class);
        Mockito.when(caster.cell()).thenReturn(map.get(123));

        BattlefieldObject bo = Mockito.mock(BattlefieldObject.class);
        Mockito.when(bo.cell()).thenReturn(map.get(123));
        Mockito.when(bo.size()).thenReturn(2);
        Mockito.when(bo.caster()).thenReturn(caster);
        Mockito.when(bo.refresh()).thenReturn(false);
        objects.add(bo);

        objects.onStartTurn(caster);
        Mockito.verify(bo).refresh();
        Mockito.verify(bo).disappear();
        Mockito.verify(bo, Mockito.never()).onStartTurnInArea(caster);
    }

    @Test
    void removeObjectsIf() {
        Fighter caster = Mockito.mock(Fighter.class);
        Fighter other = Mockito.mock(Fighter.class);

        BattlefieldObject bo1 = Mockito.mock(BattlefieldObject.class);
        BattlefieldObject bo2 = Mockito.mock(BattlefieldObject.class);
        BattlefieldObject bo3 = Mockito.mock(BattlefieldObject.class);

        Mockito.when(bo1.caster()).thenReturn(caster);
        Mockito.when(bo2.caster()).thenReturn(other);
        Mockito.when(bo3.caster()).thenReturn(caster);

        objects.add(bo1);
        objects.add(bo2);
        objects.add(bo3);

        objects.removeIf(bo -> bo.caster() == caster);

        assertIterableEquals(objects, Collections.singletonList(bo2));

        Mockito.verify(bo1).disappear();
        Mockito.verify(bo2, Mockito.never()).disappear();
        Mockito.verify(bo3).disappear();
    }

    @Test
    void removeObject() {
        Fighter caster = Mockito.mock(Fighter.class);
        Fighter other = Mockito.mock(Fighter.class);

        BattlefieldObject bo1 = Mockito.mock(BattlefieldObject.class);
        BattlefieldObject bo2 = Mockito.mock(BattlefieldObject.class);
        BattlefieldObject bo3 = Mockito.mock(BattlefieldObject.class);

        Mockito.when(bo1.caster()).thenReturn(caster);
        Mockito.when(bo2.caster()).thenReturn(other);
        Mockito.when(bo3.caster()).thenReturn(caster);

        objects.add(bo1);
        objects.add(bo2);
        objects.add(bo3);

        objects.remove(bo2);

        assertIterableEquals(objects, Arrays.asList(bo1, bo3));

        Mockito.verify(bo1, Mockito.never()).disappear();
        Mockito.verify(bo2).disappear();
        Mockito.verify(bo3, Mockito.never()).disappear();
    }

    @Test
    void anyMatch() {
        assertFalse(objects.anyMatch(bo -> true));

        Fighter caster = Mockito.mock(Fighter.class);
        Fighter other = Mockito.mock(Fighter.class);

        BattlefieldObject bo1 = Mockito.mock(BattlefieldObject.class);
        BattlefieldObject bo2 = Mockito.mock(BattlefieldObject.class);
        BattlefieldObject bo3 = Mockito.mock(BattlefieldObject.class);

        Mockito.when(bo1.caster()).thenReturn(caster);
        Mockito.when(bo2.caster()).thenReturn(other);
        Mockito.when(bo3.caster()).thenReturn(caster);

        objects.add(bo1);
        objects.add(bo2);
        objects.add(bo3);

        assertTrue(objects.anyMatch(bo -> true));
        assertTrue(objects.anyMatch(bo -> bo.caster() == caster));
        assertTrue(objects.anyMatch(bo -> bo.caster() == other));
        assertFalse(objects.anyMatch(bo -> bo.caster() == Mockito.mock(Fighter.class)));
    }

    @Test
    void shouldStopMovement() {
        assertFalse(objects.shouldStopMovement(map.get(123)));

        BattlefieldObject bo = Mockito.mock(BattlefieldObject.class);
        Mockito.when(bo.cell()).thenReturn(map.get(123));
        Mockito.when(bo.size()).thenReturn(2);
        Mockito.when(bo.isOnArea(Mockito.any(FightCell.class))).thenCallRealMethod();
        Mockito.when(bo.shouldStopMovement()).thenReturn(true);
        objects.add(bo);

        BattlefieldObject bo2 = Mockito.mock(BattlefieldObject.class);
        Mockito.when(bo2.cell()).thenReturn(map.get(132));
        Mockito.when(bo2.size()).thenReturn(2);
        Mockito.when(bo2.isOnArea(Mockito.any(FightCell.class))).thenCallRealMethod();
        Mockito.when(bo2.shouldStopMovement()).thenReturn(false);
        objects.add(bo2);

        assertTrue(objects.shouldStopMovement(map.get(123)));
        assertTrue(objects.shouldStopMovement(map.get(124)));

        assertFalse(objects.shouldStopMovement(map.get(125)));
        assertFalse(objects.shouldStopMovement(map.get(132)));
    }

    @Test
    void onEndMoveShouldTriggerObjects() {
        assertFalse(objects.shouldStopMovement(map.get(123)));

        BattlefieldObject bo1 = Mockito.mock(BattlefieldObject.class);
        Mockito.when(bo1.cell()).thenReturn(map.get(123));
        Mockito.when(bo1.size()).thenReturn(2);
        Mockito.when(bo1.isOnArea(Mockito.any(FightCell.class))).thenCallRealMethod();
        Mockito.when(bo1.isOnArea(Mockito.any(Fighter.class))).thenCallRealMethod();
        objects.add(bo1);

        BattlefieldObject bo2 = Mockito.mock(BattlefieldObject.class);
        Mockito.when(bo2.cell()).thenReturn(map.get(132));
        Mockito.when(bo2.size()).thenReturn(2);
        Mockito.when(bo2.isOnArea(Mockito.any(FightCell.class))).thenCallRealMethod();
        Mockito.when(bo2.isOnArea(Mockito.any(Fighter.class))).thenCallRealMethod();
        objects.add(bo2);

        BattlefieldObject bo3 = Mockito.mock(BattlefieldObject.class);
        Mockito.when(bo3.cell()).thenReturn(map.get(137));
        Mockito.when(bo3.size()).thenReturn(2);
        Mockito.when(bo3.isOnArea(Mockito.any(FightCell.class))).thenCallRealMethod();
        Mockito.when(bo3.isOnArea(Mockito.any(Fighter.class))).thenCallRealMethod();
        objects.add(bo3);

        Fighter fighter = Mockito.mock(Fighter.class);
        Mockito.when(fighter.cell()).thenReturn(map.get(123));

        objects.onEndMove(fighter);

        Mockito.verify(bo1).onEnterInArea(fighter);
        Mockito.verify(bo2, Mockito.never()).onEnterInArea(fighter);
        Mockito.verify(bo3).onEnterInArea(fighter);
    }
}
