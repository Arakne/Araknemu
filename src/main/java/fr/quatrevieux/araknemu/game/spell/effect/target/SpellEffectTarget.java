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

import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.team.Team;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

/**
 * Implementation of effect target using spell flags
 */
public final class SpellEffectTarget implements EffectTarget {
    public static final SpellEffectTarget DEFAULT = new SpellEffectTarget(0);

    public static final int NOT_TEAM    = 1;
    public static final int NOT_SELF    = 2;
    public static final int NOT_ENEMY   = 4;
    public static final int ONLY_INVOC  = 8;
    public static final int NOT_INVOC   = 16;
    public static final int ONLY_CASTER = 32;

    private final int flags;

    public SpellEffectTarget(int flags) {
        this.flags = flags;
    }

    @Override
    public boolean onlyCaster() {
        return check(ONLY_CASTER);
    }

    @Override
    public boolean test(FighterData caster, FighterData fighter) {
        return checkSelf(caster, fighter)
            && checkTeam(caster.team(), fighter.team())
            && checkInvocation(fighter)
        ;
    }

    @Override
    public boolean isHook() {
        return flags >= 64;
    }

    @Override
    public @NonNegative int hookId() {
        return Math.max(flags >> 6, 0);
    }

    private boolean check(int flag) {
        return (flags & flag) == flag;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        return getClass().equals(obj.getClass()) && flags == ((SpellEffectTarget) obj).flags;
    }

    @Override
    public int hashCode() {
        return Objects.hash(flags);
    }

    private boolean checkTeam(Team<?> casterTeam, Team<?> targetTeam) {
        if (check(NOT_TEAM) && casterTeam.equals(targetTeam)) {
            return false;
        }

        if (check(NOT_ENEMY) && !casterTeam.equals(targetTeam)) {
            return false;
        }

        return true;
    }

    private boolean checkInvocation(FighterData target) {
        final boolean isInvocation = target.invoked();

        if (check(ONLY_INVOC) && !isInvocation) {
            return false;
        }

        if (check(NOT_INVOC) && isInvocation) {
            return false;
        }

        return true;
    }

    private boolean checkSelf(FighterData caster, FighterData target) {
        return !check(NOT_SELF) || !caster.equals(target);
    }
}
