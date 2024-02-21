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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc;

import fr.quatrevieux.araknemu.game.fight.castable.BaseCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffEffect;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

/**
 * Buff effect for increase erosion rate of a target
 *
 * @see fr.quatrevieux.araknemu.game.fight.fighter.FighterLife#alterErosion(int) 
 */
public final class AddErosionHandler implements EffectHandler, BuffHook {
    @Override
    public void handle(FightCastScope cast, BaseCastScope<Fighter, FightCell>.EffectScope effect) {
        throw new UnsupportedOperationException("Add erosion should be used as buff");
    }

    @Override
    public void buff(FightCastScope cast, BaseCastScope<Fighter, FightCell>.EffectScope effect) {
        final SpellEffect spellEffect = effect.effect();
        final Castable action = cast.action();
        final Fighter caster = cast.caster();

        EffectValue.forEachTargets(spellEffect, caster, cast.targets(), (target, effectValue) -> {
            target.buffs().add(new Buff(
                BuffEffect.fixed(spellEffect, effectValue.value()),
                action,
                caster,
                target,
                this
            ));
        });
    }

    @Override
    public void onBuffStarted(Buff buff) {
        buff.target().life().alterErosion(buff.effect().min());
    }

    @Override
    public void onBuffTerminated(Buff buff) {
        buff.target().life().alterErosion(-buff.effect().min());
    }
}
