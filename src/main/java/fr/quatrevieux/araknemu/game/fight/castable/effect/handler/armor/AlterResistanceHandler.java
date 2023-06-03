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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.armor;

import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffEffect;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

/**
 * Buff effect for increasing or decrease magical or physical resistance
 *
 * Like {@link fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.AddCharacteristicHandler},
 * the effect value will be computed before the buff is applied, so the resistance is fixed
 *
 * Unlike {@link ReduceDamageHandler}, this effect reduce all damage, not only direct damage
 */
public final class AlterResistanceHandler implements EffectHandler, BuffHook {
    private final boolean physical;
    private final int multiplier;

    /**
     * @param physical Does this effect alter physical resistance ? If false, it alters magical resistance
     * @param multiplier The multiplier to apply to the resistance. 1 for adding resistance, -1 for reducing resistance
     */
    public AlterResistanceHandler(boolean physical, int multiplier) {
        this.physical = physical;
        this.multiplier = multiplier;
    }

    @Override
    public void handle(FightCastScope cast, FightCastScope.EffectScope effect) {
        throw new UnsupportedOperationException("AlterResistanceHandler can only be used as buff");
    }

    @Override
    public void buff(FightCastScope cast, FightCastScope.EffectScope effect) {
        final SpellEffect spellEffect = effect.effect();
        final Fighter caster = cast.caster();

        EffectValue.forEachTargets(
            spellEffect,
            caster,
            effect.targets(),
            (target, effectValue) -> target.buffs().add(new Buff(
                BuffEffect.fixed(spellEffect, effectValue.value()),
                cast.action(),
                caster,
                target,
                this
            ))
        );
    }

    @Override
    public void onDamage(Buff buff, Damage value) {
        if (value.element().physical() != physical) {
            return;
        }

        value.fixed(buff.effect().min() * multiplier);
    }

    /**
     * Create effect handler for adding physical resistance
     * So, it will reduce all suffered damage with element neutral or earth
     */
    public static AlterResistanceHandler increasePhysical() {
        return new AlterResistanceHandler(true, 1);
    }

    /**
     * Create effect handler for adding magical resistance
     * So, it will reduce all suffered damage with element air, fire or water
     */
    public static AlterResistanceHandler increaseMagical() {
        return new AlterResistanceHandler(false, 1);
    }

    /**
     * Create effect handler for reducing magical resistance
     * So, it will increase all suffered damage with element air, fire or water
     */
    public static AlterResistanceHandler reduceMagical() {
        return new AlterResistanceHandler(false, -1);
    }
}
