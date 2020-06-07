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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.turn.action.util;

import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * Base algorithm for compute criticality
 */
final public class BaseCriticalityStrategy implements CriticalityStrategy {
    /**
     * BaseCriticalityStrategy is a short life object, and the random is only used twice per instance
     */
    final static private RandomUtil RANDOM = RandomUtil.createShared();

    final private Fighter fighter;

    public BaseCriticalityStrategy(Fighter fighter) {
        this.fighter = fighter;
    }

    @Override
    public int hitRate(int base) {
        if (base <= 2) {
            return base;
        }

        base -= fighter.characteristics().get(Characteristic.CRITICAL_BONUS);
        int agility = fighter.characteristics().get(Characteristic.AGILITY);

        if (agility < 0) {
            agility = 0;
        }

        int rate = Math.min((int) ((base * 2.9901) / Math.log(agility + 12)), base);

        return Math.max(rate, 2);
    }

    @Override
    public boolean hit(int baseRate) {
        if (baseRate < 2) { // No criticality
            return false;
        }

        return RANDOM.reverseBool(hitRate(baseRate));
    }

    @Override
    public int failureRate(int base) {
        return Math.max(base - fighter.characteristics().get(Characteristic.FAIL_MALUS), 2);
    }

    @Override
    public boolean failed(int baseRate) {
        return baseRate > 0 && RANDOM.reverseBool(failureRate(baseRate));
    }
}
