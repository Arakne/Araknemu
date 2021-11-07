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

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * The state has changed
 */
public final class FighterStateChanged {
    public enum Type {
        ADD,
        REMOVE,
        UPDATE
    }

    private final Fighter fighter;
    private final int state;
    private final Type type;

    public FighterStateChanged(Fighter fighter, int state, Type type) {
        this.fighter = fighter;
        this.state = state;
        this.type = type;
    }

    public Fighter fighter() {
        return fighter;
    }

    public int state() {
        return state;
    }

    public Type type() {
        return type;
    }
}
