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

package fr.quatrevieux.araknemu.game.spell.effect.area;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CircleAreaTest extends GameBaseCase {
    private FightMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();
        map = new FightMap(container.get(MapTemplateRepository.class).get(10340));
    }

    @Test
    void getters() {
        CircleArea area = new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 2));

        assertEquals(EffectArea.Type.CIRCLE, area.type());
        assertEquals(2, area.size());
    }

    @Test
    void resolveSize0() {
        assertEquals(
            Collections.singleton(map.get(123)),
            new CircleArea(new EffectArea(EffectArea.Type.LINE, 0)).resolve(map.get(123), map.get(123))
        );
    }

    @Test
    void resolve() {
        assertCollectionEquals(
            new CircleArea(new EffectArea(EffectArea.Type.LINE, 2)).resolve(map.get(123), map.get(137)),

            map.get(123),

            map.get(109),
            map.get(95),
            map.get(138),
            map.get(153),
            map.get(137),
            map.get(151),
            map.get(108),
            map.get(93),

            map.get(124),
            map.get(152),
            map.get(122),
            map.get(94)
        );
    }

    @Test
    void resolveShouldSortByDistanceFromCenter() {
        assertCellIds(map.get(152), 0, new int[] {152});
        assertCellIds(map.get(152), 1, new int[] {152, 138, 167, 166, 137});
        assertCellIds(map.get(152), 2, new int[] {
            152,
            138, 167, 166, 137,
            124, 153, 182, 181, 180, 151, 122, 123,
        });
    }

    private void assertCellIds(FightCell center, int size, int[] cellIds) {
        Set<FightCell> cells = new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, size)).resolve(center, center);

        assertArrayEquals(
            cellIds,
            cells.stream().mapToInt(FightCell::id).toArray()
        );
    }
}
