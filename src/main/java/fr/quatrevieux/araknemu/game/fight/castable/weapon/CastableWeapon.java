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

package fr.quatrevieux.araknemu.game.fight.castable.weapon;

import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.item.type.Weapon;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Adapt weapon item to castable
 */
public final class CastableWeapon implements Castable {
    private final Weapon weapon;

    private @MonotonicNonNull List<SpellEffect> effects;
    private @MonotonicNonNull List<SpellEffect> criticalEffects;

    public CastableWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    @Override
    public List<SpellEffect> effects() {
        if (effects == null) {
            effects = weapon.weaponEffects().stream()
                .map(effect -> new CastableWeaponEffect(effect, weapon))
                .collect(Collectors.toList())
            ;
        }

        return effects;
    }

    @Override
    public List<SpellEffect> criticalEffects() {
        if (criticalEffects == null) {
            criticalEffects = weapon.weaponEffects().stream()
                .map(effect -> new CastableWeaponEffect(effect, weapon, true))
                .collect(Collectors.toList())
            ;
        }

        return criticalEffects;
    }

    @Override
    public @NonNegative int apCost() {
        return weapon.info().apCost();
    }

    @Override
    public @NonNegative int criticalHit() {
        return weapon.info().criticalRate();
    }

    @Override
    public @NonNegative int criticalFailure() {
        return weapon.info().failureRate();
    }

    @Override
    public boolean modifiableRange() {
        return false;
    }

    @Override
    public SpellConstraints constraints() {
        return new WeaponConstraints(weapon);
    }
}
