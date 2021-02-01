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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffEffect;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Alter a characteristic with buff effect
 */
public abstract class AbstractAlterCharacteristicHandler implements EffectHandler, BuffHook {
    private final Fight fight;
    private final Characteristic characteristic;

    public AbstractAlterCharacteristicHandler(Fight fight, Characteristic characteristic) {
        this.fight = fight;
        this.characteristic = characteristic;
    }

    @Override
    public void handle(CastScope cast, CastScope.EffectScope effect) {
        throw new UnsupportedOperationException("Alter characteristic effect must be used as a buff");
    }

    @Override
    public void buff(CastScope cast, CastScope.EffectScope effect) {
        final SpellEffect buffEffect = computeBuffEffect(cast, effect.effect());

        for (PassiveFighter target : effect.targets()) {
            target.buffs().add(new Buff(buffEffect, cast.action(), cast.caster(), target, this));
        }
    }

    /**
     * Compute the buff value

     * @fixme One dice for all targets, or one dice per target ?
     */
    private SpellEffect computeBuffEffect(CastScope cast, SpellEffect effect) {
        final EffectValue value = new EffectValue(effect);

        return new BuffEffect(effect, value.value());
    }

    @Override
    public void onBuffStarted(Buff buff) {
        buff.target().characteristics().alter(characteristic, value(buff));
        fight.send(ActionEffect.buff(buff, value(buff)));
    }

    @Override
    public void onBuffTerminated(Buff buff) {
        buff.target().characteristics().alter(characteristic, -value(buff));
    }

    /**
     * Get the buff effect value
     */
    protected abstract int value(Buff buff);
}
