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

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectsUtils;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;

/**
 * Buff effect for switch position between buff caster and target when the target will take damage
 * This effect will also replace the cast target
 *
 * @see CastScope#replaceTarget(PassiveFighter, PassiveFighter)
 */
public final class SwitchPositionOnAttackHandler implements EffectHandler, BuffHook {
    private final SwitchPositionApplier applier;

    public SwitchPositionOnAttackHandler(Fight fight) {
        this.applier = new SwitchPositionApplier(fight);
    }

    @Override
    public void handle(CastScope cast, CastScope.EffectScope effect) {
        throw new UnsupportedOperationException("Switch position on damage is a buff effect");
    }

    @Override
    public void buff(CastScope cast, CastScope.EffectScope effect) {
        final ActiveFighter caster = cast.caster();

        for (PassiveFighter target : effect.targets()) {
            if (!target.equals(caster)) {
                target.buffs().add(new Buff(effect.effect(), cast.action(), caster, target, this));
            }
        }
    }

    @Override
    public boolean onCastTarget(Buff buff, CastScope cast) {
        if (!isDamageCast(cast)) {
            return true;
        }

        final ActiveFighter buffCaster = buff.caster();
        final PassiveFighter target = buff.target();

        applier.apply(buffCaster, target);
        cast.replaceTarget(target, buffCaster);

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
