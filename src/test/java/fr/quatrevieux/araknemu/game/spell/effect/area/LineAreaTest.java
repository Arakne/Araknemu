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

class LineAreaTest extends GameBaseCase {
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
        LineArea area = new LineArea(new EffectArea(EffectArea.Type.LINE, 2));

        assertEquals(EffectArea.Type.LINE, area.type());
        assertEquals(2, area.size());
    }

    @Test
    void resolveSize0() {
        assertEquals(
            Collections.singleton(map.get(123)),
            new LineArea(new EffectArea(EffectArea.Type.LINE, 0)).resolve(map.get(123), map.get(456))
        );
    }

    @Test
    void resolve() {
        assertCollectionEquals(
            new LineArea(new EffectArea(EffectArea.Type.LINE, 3)).resolve(map.get(123), map.get(137)), // NORTH_WEST

            map.get(123),
            map.get(109),
            map.get(95),
            map.get(81)
        );
    }

    @Test
    void resolveWithNegativeCellId() {
        assertCollectionEquals(
            new LineArea(new EffectArea(EffectArea.Type.LINE, 3)).resolve(map.get(0), map.get(15)), // NORTH_WEST

            map.get(0)
        );
    }

    @Test
    void resolveWithTooHighCellId() {
        assertCollectionEquals(
            new LineArea(new EffectArea(EffectArea.Type.LINE, 3)).resolve(map.get(470), map.get(456)), // SOUTH_WEST

            map.get(470)
        );
    }

    @Test
    void resolveWithOutlineCell() {
        assertCollectionEquals(
            new LineArea(new EffectArea(EffectArea.Type.LINE, 5)).resolve(map.get(405), map.get(270)), // SOUTH_EAST

            map.get(405),
            map.get(420)
        );
    }

    @Test
    void resolveShouldSortByDistanceFromCenter() {
        assertCellIds(map.get(152), map.get(167), 0, new int[] {167});
        assertCellIds(map.get(152), map.get(167), 1, new int[] {167, 182});
        assertCellIds(map.get(152), map.get(167), 2, new int[] {167, 182, 197});
        assertCellIds(map.get(152), map.get(167), 3, new int[] {167, 182, 197, 212});
    }

    private void assertCellIds(FightCell from, FightCell center, int size, int[] cellIds) {
        Set<FightCell> cells = new LineArea(new EffectArea(EffectArea.Type.LINE, size)).resolve(center, from);

        assertArrayEquals(
            cellIds,
            cells.stream().mapToInt(FightCell::id).toArray()
        );
    }
}
