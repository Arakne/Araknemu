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

/**
 * Handle effect targets
 */
public interface EffectTarget {
    /**
     * Check if the effect only targets the caster
     */
    public boolean onlyCaster();

    /**
     * Check if the fighter is a valid target
     *
     * @param caster The action caster
     * @param fighter Fighter to test
     *
     * @return true if the fighter is a valid target, or false
     */
    public boolean test(FighterData caster, FighterData fighter);
}
