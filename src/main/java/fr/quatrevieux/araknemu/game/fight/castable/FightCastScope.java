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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

import java.util.List;

/**
 * Cast scope for actual fight
 */
public final class FightCastScope extends BaseCastScope<Fighter> {
    private FightCastScope(Castable action, Fighter caster, FightCell target, List<SpellEffect> effects) {
        super(action, caster, target, effects);
    }

    /**
     * Create a basic CastScope instance
     * Should be used for weapons
     */
    public static FightCastScope simple(Castable action, Fighter caster, FightCell target, List<SpellEffect> effects) {
        return new FightCastScope(action, caster, target, effects);
    }

    /**
     * Create a cast scope with probable effects (ex: Bluff)
     * This method must be used if the action has probable effects
     *
     * @see RandomEffectSelector#select(List)
     * @see SpellEffect#probability()
     */
    public static FightCastScope probable(Castable action, Fighter caster, FightCell target, List<SpellEffect> effects) {
        return new FightCastScope(action, caster, target, RandomEffectSelector.select(effects));
    }
}
