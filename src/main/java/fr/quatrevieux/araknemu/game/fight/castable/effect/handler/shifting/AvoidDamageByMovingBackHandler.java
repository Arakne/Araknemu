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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting;

import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectsUtils;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;

/**
 * Buff effect which permit to "cancel" a damage effect by moving back
 *
 * This effect is hooked before cast the spell, and remove the target if the effect is a damage,
 * and it's launch in close combat (i.e. distance = 1)
 *
 * Note: this effect is only applied on direct damage
 */
public final class AvoidDamageByMovingBackHandler implements EffectHandler, BuffHook {
    private final MoveBackApplier applier;
    private final RandomUtil random = new RandomUtil();

    public AvoidDamageByMovingBackHandler(Fight fight) {
        applier = new MoveBackApplier(fight);
    }

    @Override
    public void handle(CastScope cast, CastScope.EffectScope effect) {
        throw new UnsupportedOperationException("Avoid damage by moving back is a buff effect");
    }

    @Override
    public void buff(CastScope cast, CastScope.EffectScope effect) {
        for (PassiveFighter target : effect.targets()) {
            target.buffs().add(new Buff(effect.effect(), cast.action(), cast.caster(), target, this));
        }
    }

    @Override
    public boolean onCastTarget(Buff buff, CastScope cast) {
        if (!isDamageCast(cast) || buff.target().cell().coordinate().distance(cast.caster().cell()) != 1) {
            return true;
        }

        if (!random.bool(buff.effect().min())) {
            return true;
        }

        applier.apply(cast.caster(), buff.target(), buff.effect().max());
        cast.removeTarget(buff.target());

        return false;
    }

    /**
     * Check if the action is direct damage attack
     *
     * @param cast The action to check
     *
     * @return true if the cast can be dodged
     */
    private boolean isDamageCast(CastScope cast) {
        return cast.effects().stream()
            .map(CastScope.EffectScope::effect)
            // Should return only direct damage effects
            .anyMatch(effect -> EffectsUtils.isDamageEffect(effect.effect()) && effect.duration() == 0)
        ;
    }
}
