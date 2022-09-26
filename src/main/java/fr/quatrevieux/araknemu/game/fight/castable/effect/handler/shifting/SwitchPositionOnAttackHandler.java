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
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.ReflectedDamage;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;

/**
 * Buff effect for switch position between buff caster and target when the target will take damage
 * This effect will also replace the cast target
 *
 * @see CastScope#replaceTarget(FighterData, FighterData)
 */
public final class SwitchPositionOnAttackHandler implements EffectHandler, BuffHook {
    private final SwitchPositionApplier applier;

    public SwitchPositionOnAttackHandler(Fight fight) {
        this.applier = new SwitchPositionApplier(fight);
    }

    @Override
    public void handle(CastScope<Fighter> cast, CastScope<Fighter>.EffectScope effect) {
        throw new UnsupportedOperationException("Switch position on damage is a buff effect");
    }

    @Override
    public void buff(CastScope<Fighter> cast, CastScope<Fighter>.EffectScope effect) {
        final Fighter caster = cast.caster();

        for (Fighter target : effect.targets()) {
            if (!target.equals(caster)) {
                target.buffs().add(new Buff(effect.effect(), cast.action(), caster, target, this));
            }
        }
    }

    @Override
    public boolean onCastTarget(Buff buff, CastScope<Fighter> cast) {
        if (!isDamageCast(cast)) {
            return true;
        }

        final Fighter buffCaster = buff.caster();
        final Fighter target = buff.target();

        applier.apply(buffCaster, target);
        cast.replaceTarget(target, buffCaster);

        return false;
    }

    @Override
    public void onReflectedDamage(Buff buff, ReflectedDamage damage) {
        final Fighter buffCaster = buff.caster();
        final Fighter target = buff.target();

        applier.apply(buffCaster, target);
        damage.changeTarget(buffCaster);
    }

    /**
     * Check if the action is direct damage attack
     *
     * @param cast The action to check
     *
     * @return true if the cast can be dodged
     */
    private boolean isDamageCast(CastScope<Fighter> cast) {
        return cast.effects().stream()
            .map(CastScope.EffectScope::effect)
            // Should return only direct damage effects
            .anyMatch(effect -> EffectsUtils.isDamageEffect(effect.effect()) && effect.duration() == 0)
        ;
    }
}
