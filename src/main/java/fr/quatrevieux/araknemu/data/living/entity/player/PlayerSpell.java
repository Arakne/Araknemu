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

package fr.quatrevieux.araknemu.data.living.entity.player;

/**
 * Save player spell data
 */
public final class PlayerSpell {
    public static final int DEFAULT_POSITION = 63;

    private final int playerId;
    private final int spellId;
    private final boolean classSpell;
    private int level;
    private int position;

    public PlayerSpell(int playerId, int spellId, boolean classSpell, int level, int position) {
        this.playerId = playerId;
        this.spellId = spellId;
        this.classSpell = classSpell;
        this.level = level;
        this.position = position;
    }

    public PlayerSpell(int playerId, int spellId, boolean classSpell) {
        this(playerId, spellId, classSpell, 1, DEFAULT_POSITION);
    }

    public int playerId() {
        return playerId;
    }

    public int spellId() {
        return spellId;
    }

    public boolean classSpell() {
        return classSpell;
    }

    public int level() {
        return level;
    }

    public int position() {
        return position;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
