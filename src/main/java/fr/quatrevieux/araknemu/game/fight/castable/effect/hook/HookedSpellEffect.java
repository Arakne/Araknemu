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

import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.SpellEffectArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.EffectTarget;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import org.checkerframework.checker.index.qual.GTENegativeOne;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Decorate a spell effect to remove the area and target,
 * so it can be safely used to apply an effect from a hook (the effect will be applied only on the target of the buff)
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler#applyFromHook(Buff)
 *
 */
public final class HookedSpellEffect implements SpellEffect {
    private final SpellEffect effect;

    public HookedSpellEffect(SpellEffect effect) {
        this.effect = effect;
    }

    @Override
    public int effect() {
        return effect.effect();
    }

    @Override
    @NonNegative
    public int min() {
        return effect.min();
    }

    @Override
    @NonNegative
    public int max() {
        return effect.max();
    }

    @Override
    public int boost() {
        return effect.boost();
    }

    @Override
    public int special() {
        return effect.special();
    }

    @Override
    @GTENegativeOne
    public int duration() {
        return effect.duration();
    }

    @Override
    @NonNegative
    public int probability() {
        return effect.probability();
    }

    @Override
    public String text() {
        return effect.text();
    }

    @Override
    public SpellEffectArea area() {
        return new CellArea();
    }

    @Override
    public EffectTarget target() {
        return SpellEffectTarget.DEFAULT;
    }

    @Override
    public boolean trap() {
        return effect.trap();
    }
}
