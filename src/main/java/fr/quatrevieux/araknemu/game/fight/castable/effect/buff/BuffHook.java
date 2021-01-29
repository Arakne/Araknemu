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

package fr.quatrevieux.araknemu.game.fight.castable.effect.buff;

import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;

/**
 * Hook action for apply buff effects
 */
public interface BuffHook {
    /**
     * Apply effect when fighter turn is started
     *
     * @return False the the fighter cannot start the turn
     */
    default public boolean onStartTurn(Buff buff) {
        return true;
    }

    /**
     * Apply effect on turn ending
     */
    default public void onEndTurn(Buff buff) {}

    /**
     * Start the buff
     */
    default public void onBuffStarted(Buff buff) {}

    /**
     * The buff is terminated (buff expired, debuff...)
     */
    default public void onBuffTerminated(Buff buff) {}

    /**
     * The fighter is a target of a cast
     */
    default public void onCastTarget(Buff buff, CastScope cast) {}

    /**
     * The fighter will take damages
     */
    default public void onDamage(Buff buff, Damage value) {}
}
