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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.heal;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectsUtils;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.AbstractAttenuableAreaEffectHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Handle basic heal effect
 *
 * This effect is boosted by {@link Characteristic#INTELLIGENCE} and {@link Characteristic#HEALTH_BOOST}
 */
public final class HealHandler extends AbstractAttenuableAreaEffectHandler implements EffectHandler, BuffHook {
    public HealHandler(Fight fight) {
        super(fight);
    }

    @Override
    protected boolean applyOnTarget(FightCastScope cast, SpellEffect effect, Fighter target, EffectValue value, @NonNegative int distance) {
        final Fighter caster = cast.caster();

        value
            .percent(caster.characteristics().get(Characteristic.INTELLIGENCE))
            .fixed(caster.characteristics().get(Characteristic.HEALTH_BOOST))
        ;

        target.life().heal(caster, EffectsUtils.applyDistanceAttenuation(value.value(), distance));

        return true;
    }

    @Override
    public void buff(FightCastScope cast, FightCastScope.EffectScope effect) {
        for (Fighter target : effect.targets()) {
            target.buffs().add(new Buff(effect.effect(), cast.action(), cast.caster(), target, this));
        }
    }

    @Override
    public boolean onStartTurn(Buff buff) {
        final Fighter caster = buff.caster();
        final SpellEffect effect = buff.effect();
        final Fighter target = buff.target();

        final EffectValue value = EffectValue.create(effect, caster, target)
            .percent(caster.characteristics().get(Characteristic.INTELLIGENCE))
            .fixed(caster.characteristics().get(Characteristic.HEALTH_BOOST))
        ;

        target.life().heal(caster, value.value());
        return !target.dead(); // Fighter may be killed by a hook after heal
    }
}
