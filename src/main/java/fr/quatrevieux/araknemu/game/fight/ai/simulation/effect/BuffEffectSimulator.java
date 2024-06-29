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

package fr.quatrevieux.araknemu.game.fight.ai.simulation.effect;

import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;

/**
 * Base type for simulate a buff effect on different hooks
 */
public interface BuffEffectSimulator {
    /**
     * Apply armor effect, or any other effect that can reduce the damage
     *
     * @param buff The armor buff
     * @param target The simulated target
     * @param damage Computed damage before reduction
     *
     * @return The reduced damage
     * @see fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator#applyReduceableDamageBuffs(FighterData, Damage) Caller of this method
     */
    public default Damage onReduceableDamage(Buff buff, FighterData target, Damage damage) {
        return damage;
    }
}
