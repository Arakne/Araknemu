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
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Decrease the sight of the spell caster
 *
 * @see Characteristic#SIGHT_BOOST
 * @see DecreaseCasterSightHandler Reverse effect
 */
public final class DecreaseCasterSightHandler implements EffectHandler, BuffHook {
    private final Fight fight;

    public DecreaseCasterSightHandler(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void handle(CastScope<Fighter> cast, CastScope<Fighter>.EffectScope effect) {
        throw new UnsupportedOperationException("Alter characteristic effect must be used as a buff");
    }

    @Override
    public void buff(CastScope<Fighter> cast, CastScope<Fighter>.EffectScope effect) {
        final SpellEffect spellEffect = effect.effect();
        final Fighter caster = cast.caster();

        caster.buffs().add(new Buff(
            BuffEffect.fixed(spellEffect, EffectValue.create(spellEffect, caster, caster).value()),
            cast.action(),
            caster,
            caster,
            this
        ));
    }

    @Override
    public void onBuffStarted(Buff buff) {
        final int value = buff.effect().min();
        final FighterData target = buff.target();

        target.characteristics().alter(Characteristic.SIGHT_BOOST, -value);
        fight.send(ActionEffect.decreaseSight(target, target, value, buff.remainingTurns()));
    }

    @Override
    public void onBuffTerminated(Buff buff) {
        buff.target().characteristics().alter(Characteristic.SIGHT_BOOST, buff.effect().min());
    }
}
