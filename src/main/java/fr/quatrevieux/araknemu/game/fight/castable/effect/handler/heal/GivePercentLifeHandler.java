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

import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;

/**
 * Heal fighters by giving its own life, in percent of current life
 */
public final class GivePercentLifeHandler implements EffectHandler {
    @Override
    public void handle(FightCastScope cast, FightCastScope.EffectScope effect) {
        final FighterData caster = cast.caster();
        final int heal = EffectValue.create(effect.effect(), caster, caster).value() * caster.life().current() / 100;

        caster.life().alter(caster, -heal);

        for (FighterData target : effect.targets()) {
            if (!target.equals(caster)) {
                target.life().alter(caster, heal);
            }
        }
    }

    @Override
    public void buff(FightCastScope cast, FightCastScope.EffectScope effect) {
        throw new UnsupportedOperationException("Cannot use give percent life effect as buff");
    }
}
