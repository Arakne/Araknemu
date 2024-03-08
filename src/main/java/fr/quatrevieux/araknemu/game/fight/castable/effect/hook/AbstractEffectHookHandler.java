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
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;

/**
 * Base implementation for effect hook handlers
 * It will simply add a buff to the target, with a custom hook declared in the subclass
 *
 * Usage:
 * <pre>{@code
 * public class MyHookHandler extends AbstractEffectHookHandler {
 *     @Override
 *     protected BuffHook createHook(EffectHandler handler) {
 *         return new BuffHook() {
 *             // Apply the effect when the target cast any spell or close combat
 *             // The buff is created by AbstractEffectHookHandler::apply()
 *             @Override
 *             public void onCast(Buff buff, FightCastScope cast) {
 *                 // Simply call the effect handler with the buff
 *                 handler.applyFromHook(buff);
 *             }
 *         };
 *     }
 * }
 * }</pre>
 */
public abstract class AbstractEffectHookHandler implements EffectHookHandler {
    @Override
    public final boolean apply(EffectHandler handler, FightCastScope cast, BaseCastScope<Fighter, FightCell>.EffectScope effect) {
        final BuffHook hook = createHook(handler);

        for (Fighter target : effect.targets()) {
            target.buffs().add(new Buff(effect.effect(), cast.action(), cast.caster(), target, hook));
        }

        return false;
    }

    protected abstract BuffHook createHook(EffectHandler handler);
}
