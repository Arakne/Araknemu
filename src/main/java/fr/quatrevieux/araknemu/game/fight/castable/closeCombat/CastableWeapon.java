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

import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.item.type.Weapon;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Adapt weapon item to castable
 */
public final class CastableWeapon implements Castable {
    private final Weapon weapon;
    private @NonNegative int ability;
    private @Nullable List<SpellEffect> effects;
    private @Nullable List<SpellEffect> criticalEffects;

    public CastableWeapon(@NonNegative int ability, Weapon weapon) {
        this.ability = ability;
        this.weapon = weapon;
    }

    @Override
    public List<SpellEffect> effects() {
        List<SpellEffect> effects = this.effects;

        if (effects == null) {
            this.effects = effects = weapon.weaponEffects().stream()
                .map(effect -> new CastableWeaponEffect(effect, weapon, ability))
                .collect(Collectors.toList())
            ;
        }

        return effects;
    }

    @Override
    public List<SpellEffect> criticalEffects() {
        List<SpellEffect> effects = this.criticalEffects;

        if (effects == null) {
            this.criticalEffects = effects = weapon.weaponEffects().stream()
                .map(effect -> new CastableWeaponEffect(effect, weapon, ability, true))
                .collect(Collectors.toList())
            ;
        }

        return effects;
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

    /**
     * Get the current type of the weapon
     */
    public ItemType weaponType() {
        return weapon.type();
    }

    /**
     * Get the current ability of the weapon
     *
     * This value is a percent of damage (e.g. 80 = 80%), applied to weapon effects.
     * It is initialized using {@link fr.quatrevieux.araknemu.game.player.race.GamePlayerRace#weaponAbility(ItemType)},
     * and can be modified using {@link CastableWeapon#increaseAbility(int)} and {@link CastableWeapon#decreaseAbility(int)}
     */
    public @NonNegative int ability() {
        return ability;
    }

    /**
     * Increase the ability of the weapon
     *
     * @param boost The boost to apply in percent (e.g. 50 = +50% of damage)
     */
    public void increaseAbility(@NonNegative int boost) {
        ability += boost;
        criticalEffects = null;
        effects = null;
    }

    /**
     * Decrease the ability of the weapon
     * If the malus is greater than the current ability, the ability will be set to 0 (so the weapon will be lowered to 1)
     *
     * @param malus The malus to apply in percent (e.g. 50 = -50% of damage)
     */
    public void decreaseAbility(@NonNegative int malus) {
        ability = Math.max(0, ability - malus);

        criticalEffects = null;
        effects = null;
    }

    @Override
    public String toString() {
        return "Weapon{id=" + weapon.template().id() + ", name=" + weapon.template().name() + "}";
    }
}
