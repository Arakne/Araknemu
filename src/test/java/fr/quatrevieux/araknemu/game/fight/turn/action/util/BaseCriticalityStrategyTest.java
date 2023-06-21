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

package fr.quatrevieux.araknemu.game.fight.turn.action.util;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BaseCriticalityStrategyTest extends FightBaseCase {
    private BaseCriticalityStrategy strategy;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        createFight();

        strategy = new BaseCriticalityStrategy();
    }

    @Test
    void hitRateWithoutBonus() {
        assertEquals(25, strategy.hitRate(player.fighter(), 25));
    }

    @Test
    void hitRateWithFixedBonus() {
        player.properties().characteristics().base().set(Characteristic.CRITICAL_BONUS, 10);

        assertEquals(15, strategy.hitRate(player.fighter(), 25));
    }

    @Test
    void hitRateWithAgility() {
        player.properties().characteristics().base().set(Characteristic.AGILITY, 100);

        assertEquals(15, strategy.hitRate(player.fighter(), 25));
    }

    @Test
    void hitRate2() {
        assertEquals(2, strategy.hitRate(player.fighter(), 2));
    }

    @Test
    void hitRate0() {
        assertEquals(1, strategy.hitRate(player.fighter(), 0));
    }

    @Test
    void failureRate() {
        assertEquals(100, strategy.failureRate(player.fighter(), 100));
    }

    @Test
    void failureRateWithMalus() {
        player.properties().characteristics().base().set(Characteristic.FAIL_MALUS, 10);

        assertEquals(90, strategy.failureRate(player.fighter(), 100));
    }
}
