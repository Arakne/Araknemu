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

package fr.quatrevieux.araknemu.game.fight.castable.closeCombat;

import fr.quatrevieux.araknemu.game.item.effect.WeaponEffect;
import fr.quatrevieux.araknemu.game.item.type.Weapon;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.SpellEffectArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.EffectTarget;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Effect for a weapon
 */
public final class CastableWeaponEffect implements SpellEffect {
    private final WeaponEffect effect;
    private final Weapon weapon;
    private final boolean critical;

    public CastableWeaponEffect(WeaponEffect effect, Weapon weapon) {
        this(effect, weapon, false);
    }

    public CastableWeaponEffect(WeaponEffect effect, Weapon weapon, boolean critical) {
        this.effect = effect;
        this.weapon = weapon;
        this.critical = critical;
    }

    @Override
    public int effect() {
        return effect.effect().id();
    }

    @Override
    public @NonNegative int min() {
        return applyCriticalBonus(effect.min());
    }

    @Override
    public @NonNegative int max() {
        return applyCriticalBonus(effect.max());
    }

    @Override
    public int special() {
        return effect.extra();
    }

    @Override
    public @NonNegative int duration() {
        return 0;
    }

    @Override
    public @NonNegative int probability() {
        return 0;
    }

    @Override
    public String text() {
        return "";
    }

    @Override
    public SpellEffectArea area() {
        return weapon.effectArea();
    }

    @Override
    public EffectTarget target() {
        return WeaponEffectTarget.INSTANCE;
    }

    private @NonNegative int applyCriticalBonus(@NonNegative int base) {
        if (critical) {
            return base + weapon.info().criticalBonus();
        }

        return base;
    }
}
