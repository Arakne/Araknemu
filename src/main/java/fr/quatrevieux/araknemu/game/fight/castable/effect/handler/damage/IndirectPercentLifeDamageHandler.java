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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;

/**
 * Handle damage based on the current caster life
 *
 * Compute damage in same way as {@link PercentLifeDamageHandler} in buff (i.e. poison) context
 * Damage cannot be boosted, but can be reduced using armor because it's related to an element
 *
 * Only indirect buffs are called
 */
public final class IndirectPercentLifeDamageHandler implements EffectHandler {
    private final DamageApplier applier;

    public IndirectPercentLifeDamageHandler(Element element, Fight fight) {
        this.applier = new DamageApplier(element, fight);
    }

    @Override
    public void handle(CastScope cast, CastScope.EffectScope effect) {
        final ActiveFighter caster = cast.caster();
        final int damage = caster.life().current() * (new EffectValue(effect.effect())).value() / 100;

        for (PassiveFighter target : effect.targets()) {
            applier.applyIndirectFixed(caster, damage, target);
        }
    }

    @Override
    public void buff(CastScope cast, CastScope.EffectScope effect) {
        throw new UnsupportedOperationException("Indirect percent life damage cannot be used as buff");
    }
}
