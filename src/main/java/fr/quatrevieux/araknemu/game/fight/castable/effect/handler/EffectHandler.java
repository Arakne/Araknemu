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

import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.hook.HookedSpellEffect;
import fr.quatrevieux.araknemu.game.fight.castable.validator.CastConstraintValidator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collections;

/**
 * Handle a fight effect
 */
public interface EffectHandler extends CastConstraintValidator<Castable> {
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

    /**
     * Apply the effect through a hook
     *
     * @param buff The buff to apply
     * @see fr.quatrevieux.araknemu.game.fight.castable.effect.hook.EffectHookHandler for more details
     */
    public default void applyFromHook(Buff buff) {
        final Fighter target = buff.target();
        final FightCell cell = target.cell();
        final Castable spell = buff.action();
        final SpellEffect effect = new HookedSpellEffect(buff.effect());

        final FightCastScope castScope = FightCastScope.simple(spell, buff.caster(), cell, Collections.singletonList(effect));

        handle(castScope, castScope.effects().get(0));
    }

    @Override
    public default boolean check(Turn turn, Castable castable, BattlefieldCell target) {
        return true;
    }

    @Override
    public default @Nullable Object validate(Turn turn, Castable castable, BattlefieldCell target) {
        return null;
    }
}
