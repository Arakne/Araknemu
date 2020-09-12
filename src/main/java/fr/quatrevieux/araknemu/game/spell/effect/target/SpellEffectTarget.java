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

package fr.quatrevieux.araknemu.game.spell.effect.target;

import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;

/**
 * Implementation of effect target using spell flags
 */
final public class SpellEffectTarget implements EffectTarget {
    final static public SpellEffectTarget DEFAULT = new SpellEffectTarget(0);

    final static public int NOT_TEAM    = 1;
    final static public int NOT_SELF    = 2;
    final static public int NOT_ENEMY   = 4;
    final static public int ONLY_INVOC  = 8;
    final static public int NOT_INVOC   = 16;
    final static public int ONLY_CASTER = 32;

    final private int flags;

    public SpellEffectTarget(int flags) {
        this.flags = flags;
    }

    @Override
    public boolean onlyCaster() {
        return check(ONLY_CASTER);
    }

    @Override
    public boolean test(ActiveFighter caster, PassiveFighter fighter) {
        if (check(NOT_TEAM) && caster.team().equals(fighter.team())) {
            return false;
        }

        if (check(NOT_SELF) && caster.equals(fighter)) {
            return false;
        }

        if (check(NOT_ENEMY) && !caster.team().equals(fighter.team())) {
            return false;
        }

        if (check(ONLY_INVOC)) {
            return false;
        }

        return true;
    }

    private boolean check(int flag) {
        return (flags & flag) == flag;
    }

    @Override
    public boolean equals(Object obj) {
        return getClass().equals(obj.getClass()) && flags == ((SpellEffectTarget) obj).flags;
    }
}
