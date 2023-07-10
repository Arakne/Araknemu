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

package fr.quatrevieux.araknemu.network.game.out.fight;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FighterPositionsTest extends FightBaseCase {
    private List<Fighter> fighters;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMaps()
            .pushSubAreas()
            .pushAreas()
        ;

        Fight fight = createFight(true);
        fight.nextState();

        fighters = new ArrayList<>(fight.fighters().all());

        fighters.get(0).move(fight.map().get(123));
        fighters.get(1).move(fight.map().get(321));
    }

    @Test
    void generate() {
        assertEquals(
            "GIC|" + fighters.get(0).id() + ";123|" + fighters.get(1).id() + ";321",
            new FighterPositions(fighters).toString()
        );
    }

    @Test
    void generateShouldFilterDeadFighters() {
        fighters.get(1).life().kill(fighters.get(1));

        assertEquals(
            "GIC|" + fighters.get(0).id() + ";123",
            new FighterPositions(fighters).toString()
        );
    }

    @Test
    void generateWithSingleFighter() {
        assertEquals(
            "GIC|" + fighters.get(0).id() + ";123",
            new FighterPositions(fighters.get(0)).toString()
        );
    }
}
