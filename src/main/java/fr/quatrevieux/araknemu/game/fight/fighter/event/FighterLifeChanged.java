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

package fr.quatrevieux.araknemu.game.fight.fighter.event;

import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;

/**
 * Fighter life is changed
 */
public final class FighterLifeChanged {
    private final FighterData fighter;
    private final FighterData caster;
    private final int value;

    public FighterLifeChanged(FighterData fighter, FighterData caster, int value) {
        this.fighter = fighter;
        this.caster = caster;
        this.value = value;
    }

    public FighterData fighter() {
        return fighter;
    }

    public FighterData caster() {
        return caster;
    }

    /**
     * Life change value
     * Negative for loosing life, positive for heal
     */
    public int value() {
        return value;
    }
}
