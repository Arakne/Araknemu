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

import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.SpellEffectArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.EffectTarget;

/**
 * Overrides effect parameters for buff effect
 */
public final class BuffEffect implements SpellEffect {
    private final SpellEffect baseEffect;

    private final int min;
    private final int max;
    private final int special;
    private final String text;

    public BuffEffect(SpellEffect baseEffect, int min) {
        this(baseEffect, min, 0, 0);
    }

    public BuffEffect(SpellEffect baseEffect, int min, int max) {
        this(baseEffect, min, max, 0);
    }

    public BuffEffect(SpellEffect baseEffect, int min, int max, int special) {
        this(baseEffect, min, max, special, null);
    }

    public BuffEffect(SpellEffect baseEffect, int min, int max, int special, String text) {
        this.baseEffect = baseEffect;
        this.min = min;
        this.max = max;
        this.special = special;
        this.text = text;
    }

    @Override
    public int effect() {
        return baseEffect.effect();
    }

    @Override
    public int min() {
        return min;
    }

    @Override
    public int max() {
        return max;
    }

    @Override
    public int special() {
        return special;
    }

    @Override
    public int duration() {
        return baseEffect.duration();
    }

    @Override
    public int probability() {
        return 0;
    }

    @Override
    public String text() {
        return text;
    }

    @Override
    public SpellEffectArea area() {
        return baseEffect.area();
    }

    @Override
    public EffectTarget target() {
        return baseEffect.target();
    }
}
