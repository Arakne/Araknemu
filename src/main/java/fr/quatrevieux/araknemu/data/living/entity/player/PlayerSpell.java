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

import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.common.value.qual.IntRange;

/**
 * Save player spell data
 */
public final class PlayerSpell {
    public static final int DEFAULT_POSITION = 63;

    private final int playerId;
    private final  int spellId;
    private final boolean classSpell;
    private @Positive int level;
    private @IntRange(from = 1, to = 63) int position;

    public PlayerSpell(int playerId, int spellId, boolean classSpell, @Positive int level, @IntRange(from = 1, to = 63) int position) {
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

    public @Positive int level() {
        return level;
    }

    public @IntRange(from = 1, to = 63) int position() {
        return position;
    }

    public void setLevel(@Positive int level) {
        this.level = level;
    }

    public void setPosition(@IntRange(from = 1, to = 63) int position) {
        this.position = position;
    }
}
