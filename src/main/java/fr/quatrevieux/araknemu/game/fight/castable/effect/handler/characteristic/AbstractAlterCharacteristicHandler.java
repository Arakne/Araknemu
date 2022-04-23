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

import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffEffect;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

/**
 * Alter a characteristic with buff effect
 */
public abstract class AbstractAlterCharacteristicHandler implements EffectHandler, BuffHook {
    private final AlterCharacteristicHook hook;
    private final boolean canBeDispelled;

    public AbstractAlterCharacteristicHandler(AlterCharacteristicHook hook, boolean canBeDispelled) {
        this.hook = hook;
        this.canBeDispelled = canBeDispelled;
    }

    @Override
    public void handle(CastScope cast, CastScope.EffectScope effect) {
        throw new UnsupportedOperationException("Alter characteristic effect must be used as a buff");
    }

    @Override
    public void buff(CastScope cast, CastScope.EffectScope effect) {
        final SpellEffect spellEffect = effect.effect();
        final ActiveFighter caster = cast.caster();

        EffectValue.forEachTargets(
            spellEffect,
            caster,
            effect.targets(),
            (target, effectValue) -> target.buffs().add(new Buff(
                BuffEffect.fixed(spellEffect, effectValue.value()),
                cast.action(),
                caster,
                target,
                this,
                canBeDispelled
            ))
        );
    }

    @Override
    public void onBuffStarted(Buff buff) {
        hook.onBuffStarted(buff);
    }

    @Override
    public void onBuffTerminated(Buff buff) {
        hook.onBuffTerminated(buff);
    }
}
