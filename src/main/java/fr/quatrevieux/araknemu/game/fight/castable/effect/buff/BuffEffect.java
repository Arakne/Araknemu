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

    private final int effect;
    private final int value;

    private BuffEffect(SpellEffect baseEffect, int effect, int value) {
        this.baseEffect = baseEffect;
        this.effect = effect;
        this.value = value;
    }

    @Override
    public int effect() {
        return effect;
    }

    @Override
    public int min() {
        return value;
    }

    @Override
    public int max() {
        return 0;
    }

    @Override
    public int special() {
        return 0;
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
        return "";
    }

    @Override
    public SpellEffectArea area() {
        return baseEffect.area();
    }

    @Override
    public EffectTarget target() {
        return baseEffect.target();
    }

    /**
     * Set a fixed effect value
     *
     * @param baseEffect The spell effect
     * @param value The applied value
     */
    public static BuffEffect fixed(SpellEffect baseEffect, int value) {
        return new BuffEffect(baseEffect, baseEffect.effect(), value);
    }

    /**
     * Define an effect with custom effect id and a fixed value
     *
     * @param baseEffect The spell effect
     * @param effect The overridden effect id
     * @param value The applied value
     */
    public static BuffEffect withCustomEffect(SpellEffect baseEffect, int effect, int value) {
        return new BuffEffect(baseEffect, effect, value);
    }
}
