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

package fr.quatrevieux.araknemu.game.fight.ai.util;

import fr.quatrevieux.araknemu.game.fight.ai.AI;

import java.util.function.Predicate;

/**
 * Utility class for create AI predicates
 */
public final class Predicates {
    private Predicates() {
        // Disabled
    }

    /**
     * Check if there is at least one enemy in range of one attack spell
     *
     * Note: this method will only check for simple damage or steal life spells, and ignore spell constraints
     */
    public static Predicate<AI> hasEnemyInRange() {
        return ai -> ai.helper().spells()
            .withEffect(effect -> effect.effect() >= 91 && effect.effect() <= 100)
            .anyMatch(spell -> ai.helper().enemies().inRange(spell.constraints().range()).findAny().isPresent())
        ;
    }

    /**
     * Check if there is at least one ally on the fight
     */
    public static Predicate<AI> hasAllies() {
        return ai -> ai.helper().allies().stream().findAny().isPresent();
    }
}
