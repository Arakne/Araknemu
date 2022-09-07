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

package fr.quatrevieux.araknemu.game.spell.effect;

import fr.quatrevieux.araknemu.data.value.SpellTemplateEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.SpellEffectArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.EffectTarget;
import org.checkerframework.checker.index.qual.GTENegativeOne;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Adapt {@link SpellTemplateEffect}
 */
public final class SpellTemplateEffectAdapter implements SpellEffect {
    private final SpellTemplateEffect effect;
    private final SpellEffectArea area;
    private final EffectTarget target;

    public SpellTemplateEffectAdapter(SpellTemplateEffect effect, SpellEffectArea area, EffectTarget target) {
        this.effect = effect;
        this.area = area;
        this.target = target;
    }

    @Override
    public int effect() {
        return effect.effect();
    }

    @Override
    public @NonNegative int min() {
        return effect.min();
    }

    @Override
    public @NonNegative int max() {
        return effect.max();
    }

    @Override
    public int special() {
        return effect.special();
    }

    @Override
    public @GTENegativeOne int duration() {
        return effect.duration();
    }

    @Override
    public @NonNegative int probability() {
        return effect.probability();
    }

    @Override
    public String text() {
        return effect.text();
    }

    @Override
    public SpellEffectArea area() {
        return area;
    }

    @Override
    public EffectTarget target() {
        return target;
    }
}
