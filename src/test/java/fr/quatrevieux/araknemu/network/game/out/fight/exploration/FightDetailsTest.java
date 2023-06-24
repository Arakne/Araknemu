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

package fr.quatrevieux.araknemu.network.game.out.fight.exploration;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
class FightDetailsTest extends FightBaseCase {
    @Test
    void generate() throws Exception {
        dataSet.pushMaps().pushSubAreas().pushAreas();

        Fight fight = createFight(false);

        assertEquals("fD1|Bob~50|Other~1", new FightDetails(fight).toString());
    }

    @Test
    void generateWithMultipleFighterInTeam() throws Exception {
        dataSet.pushMaps().pushSubAreas().pushAreas();

        Fight fight = createFight();

        fight.team(0).join(makePlayerFighter(makeSimpleGamePlayer(10)));

        assertEquals("fD1|Bob~50;PLAYER_10~1|Other~1", new FightDetails(fight).toString());
    }
}