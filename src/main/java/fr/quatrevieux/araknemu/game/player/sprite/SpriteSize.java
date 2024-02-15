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

package fr.quatrevieux.araknemu.game.player.sprite;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Store size of the sprite
 */
public final class SpriteSize {
    public static final SpriteSize DEFAULT = new SpriteSize(100, 100);

    private final int x;
    private final int y;
    private @Nullable String stringCache = null;

    public SpriteSize(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    @Override
    public String toString() {
        final String stringCache = this.stringCache;

        if (stringCache != null) {
            return stringCache;
        }

        return this.stringCache = x + "x" + y;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final SpriteSize that = (SpriteSize) o;

        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return (17 + x) * 31 + y;
    }
}
