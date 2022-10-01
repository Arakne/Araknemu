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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler;

import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;

/**
 * Handle a fight effect
 */
public interface EffectHandler {
    /**
     * Handle the effect on the target
     */
    public void handle(FightCastScope cast, FightCastScope.EffectScope effect);

    /**
     * Apply a buff to the target
     *
     * @param cast The cast action arguments
     * @param effect The effect to apply
     */
    public void buff(FightCastScope cast, FightCastScope.EffectScope effect);
}
