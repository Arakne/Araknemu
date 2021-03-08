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
 * Copyright (c) 2017-2021 Vincent Quatrevieux Jean-Alexandre Valentin
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope.EffectScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Handle the dispel effect (i.e. remove all active buffs of a fighter)
 * The effect ID is 132
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs#removeAll()
 */
public final class DispelHandler implements EffectHandler {
    private final Fight fight;

    public DispelHandler(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void buff(CastScope cast, EffectScope effect) {
        throw new UnsupportedOperationException("Cannot dispel buffs from a buff");
    }

    @Override
    public void handle(CastScope cast, EffectScope effect) {
        for (PassiveFighter fighter : cast.targets()) {
            fighter.buffs().removeAll();
            fight.send(ActionEffect.dispelBuffs(cast.caster(), fighter));
        }
    }
}
