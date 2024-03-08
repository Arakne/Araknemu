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

package fr.quatrevieux.araknemu.game.fight.castable.effect.hook;

import fr.quatrevieux.araknemu.game.fight.castable.BaseCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;

/**
 * Base type for declare an effect applied through a buff hook,
 * instead to be applied directly
 *
 * The expecting behavior of this handler is:
 * - Create buff hook instance, taking the effect handler as parameter.
 * - Implements the required hook methods, and call {@link EffectHandler#applyFromHook(Buff)} to apply the effect.
 * - Add a buff to each target, following the original effect target.
 *   The buff is created and added by the implementation of this handler, and take the previously created hook as parameter.
 * - Return false to prevent the effect to be applied directly, or true to apply it.
 */
public interface EffectHookHandler {
    /**
     * Initialize the hook for the given effect handler, and targets
     * Generally, this method should create a new buff hook instance, and add a buff to each target
     *
     * @param handler The resolved effect handler, which will be used to apply the effect
     * @param cast The cast action arguments. Same as {@link EffectHandler#handle(FightCastScope, BaseCastScope.EffectScope)} first parameter
     * @param effect The effect to apply through the hook. Same as {@link EffectHandler#handle(FightCastScope, BaseCastScope.EffectScope)} second parameter
     *
     * @return true to apply the effect directly, or false to prevent it (so the effect is only applied through the hook)
     */
    public boolean apply(EffectHandler handler, FightCastScope cast, FightCastScope.EffectScope effect);
}
