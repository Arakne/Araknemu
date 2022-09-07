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

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FighterPositionsTest extends GameBaseCase {
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

        fighters = Arrays.asList(
            new PlayerFighter(gamePlayer(true)),
            new PlayerFighter(makeOtherPlayer())
        );

        FightMap map = container.get(FightService.class).map(
            container.get(ExplorationMapService.class).load(10340)
        );

        fighters.get(0).move(map.get(123));
        fighters.get(1).move(map.get(321));
    }

    @Test
    void generate() {
        assertEquals(
            "GIC|" + fighters.get(0).id() + ";123|" + fighters.get(1).id() + ";321",
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
