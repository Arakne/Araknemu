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

package fr.quatrevieux.araknemu.game.fight.castable.weapon;

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.game.item.type.Weapon;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;

/**
 * Cast constraints for a weapon
 */
final public class WeaponConstraints implements SpellConstraints {
    final private Weapon weapon;

    public WeaponConstraints(Weapon weapon) {
        this.weapon = weapon;
    }

    @Override
    public Interval range() {
        return weapon.info().range();
    }

    @Override
    public boolean lineLaunch() {
        return false;
    }

    @Override
    public boolean lineOfSight() {
        return true;
    }

    @Override
    public boolean freeCell() {
        return false;
    }

    @Override
    public int launchPerTurn() {
        return 0;
    }

    @Override
    public int launchPerTarget() {
        return 0;
    }

    @Override
    public int launchDelay() {
        return 0;
    }

    @Override
    public int[] requiredStates() {
        return new int[] {};
    }

    @Override
    public int[] forbiddenStates() {
        return new int[] {1, 3, 18, 42};
    }
}
