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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CastTargetsTest {
    @Test
    void empty() {
        assertSame(CastTargets.empty(), CastTargets.empty());

        assertEquals(Collections.emptyList(), toList(CastTargets.empty()));
        assertForEach(CastTargets.empty());

        assertSame(CastTargets.empty(), CastTargets.builder().build());

        Iterator<Fighter> iterator = CastTargets.<Fighter>empty().iterator();
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void singleton() {
        Fighter fighter = Mockito.mock(Fighter.class);
        BattlefieldCell cell = Mockito.mock(BattlefieldCell.class);

        CastTargets<Fighter> targets = CastTargets.one(fighter, cell);

        assertEquals(Collections.singletonList(fighter), toList(targets));
        assertForEach(targets, Pair.of(fighter, cell));

        CastTargets.Builder<Fighter> builder = CastTargets.builder();
        builder.add(fighter, cell);

        targets = builder.build();

        assertEquals(Collections.singletonList(fighter), toList(targets));
        assertForEach(targets, Pair.of(fighter, cell));

        Iterator<Fighter> iterator = targets.iterator();
        assertTrue(iterator.hasNext());
        assertSame(fighter, iterator.next());
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void pair() {
        Fighter fighter1 = Mockito.mock(Fighter.class);
        BattlefieldCell cell1 = Mockito.mock(BattlefieldCell.class);

        Fighter fighter2 = Mockito.mock(Fighter.class);
        BattlefieldCell cell2 = Mockito.mock(BattlefieldCell.class);

        CastTargets.Builder<Fighter> builder = CastTargets.builder();

        builder.add(fighter1, cell1);
        builder.add(fighter2, cell2);

        CastTargets<Fighter> targets = builder.build();

        assertEquals(Arrays.asList(fighter1, fighter2), toList(targets));
        assertForEach(targets, Pair.of(fighter1, cell1), Pair.of(fighter2, cell2));

        Iterator<Fighter> iterator = targets.iterator();
        assertTrue(iterator.hasNext());
        assertSame(fighter1, iterator.next());
        assertTrue(iterator.hasNext());
        assertSame(fighter2, iterator.next());
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void buildManyWithoutRealloc() {
        Fighter fighter1 = Mockito.mock(Fighter.class);
        BattlefieldCell cell1 = Mockito.mock(BattlefieldCell.class);

        Fighter fighter2 = Mockito.mock(Fighter.class);
        BattlefieldCell cell2 = Mockito.mock(BattlefieldCell.class);

        Fighter fighter3 = Mockito.mock(Fighter.class);
        BattlefieldCell cell3 = Mockito.mock(BattlefieldCell.class);

        CastTargets.Builder<Fighter> builder = CastTargets.builder();

        builder.add(fighter1, cell1);
        builder.add(fighter2, cell2);
        builder.add(fighter3, cell3);

        CastTargets<Fighter> targets = builder.build();

        assertEquals(Arrays.asList(fighter1, fighter2, fighter3), toList(targets));
        assertForEach(targets, Pair.of(fighter1, cell1), Pair.of(fighter2, cell2), Pair.of(fighter3, cell3));
    }

    @Test
    void buildMany() {
        List<Fighter> fighters = Stream.generate(() -> Mockito.mock(Fighter.class)).limit(100).collect(Collectors.toList());
        List<BattlefieldCell> cells = Stream.generate(() -> Mockito.mock(BattlefieldCell.class)).limit(100).collect(Collectors.toList());

        CastTargets.Builder<Fighter> builder = CastTargets.builder();

        for(int i = 0; i < fighters.size(); ++i){
            builder.add(fighters.get(i), cells.get(i));
        }

        CastTargets<Fighter> targets = builder.build();

        assertEquals(fighters, toList(targets));
        assertForEach(targets, fighters.stream().map(fighter -> Pair.of(fighter, cells.get(fighters.indexOf(fighter)))).toArray(Pair[]::new));
    }

    @Test
    void forEachWithReturnFalse() {
        Fighter fighter1 = Mockito.mock(Fighter.class);
        BattlefieldCell cell1 = Mockito.mock(BattlefieldCell.class);

        Fighter fighter2 = Mockito.mock(Fighter.class);
        BattlefieldCell cell2 = Mockito.mock(BattlefieldCell.class);

        CastTargets.Builder<Fighter> builder = CastTargets.builder();

        builder.add(fighter1, cell1);
        builder.add(fighter2, cell2);

        CastTargets<Fighter> targets = builder.build();

        List<Pair<Fighter, BattlefieldCell>> actual = new ArrayList<>();

        targets.forEach((fighter, cell) -> {
            actual.add(Pair.of(fighter, cell));
            return false;
        });

        assertEquals(Collections.singletonList(Pair.of(fighter1, cell1)), actual);
    }

    public List<Fighter> toList(CastTargets<Fighter> targets){
        List<Fighter> list = new ArrayList<>();
        for(Fighter fighter : targets){
            list.add(fighter);
        }
        return list;
    }

    public void assertForEach(CastTargets<Fighter> targets, Pair<Fighter, BattlefieldCell> ... pairs){
        List<Pair<Fighter, BattlefieldCell>> actual = new ArrayList<>();

        targets.forEach((fighter, cell) -> actual.add(Pair.of(fighter, cell)));

        assertEquals(Arrays.asList(pairs), actual);
    }
}
