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
 * Copyright (c) 2017-2026 Leor Finacre
 */

package fr.quatrevieux.araknemu.data.constant;

import org.checkerframework.checker.index.qual.NonNegative;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * List of emotes for Dofus 1.29.1
 */
public enum Emote {
    NONE(0, false),
    SIT(1, true),
    BYE(2, false),
    APPL(3, false),
    MAD(4, false),
    FEAR(5, false),
    WIP(6, false),
    PIPO(7, false),
    OUPS(8, false),
    HI(9, false),
    KISS(10, false),
    PFC1(11, false),
    PFC2(12, false),
    PFC3(13, false),
    CROSS(14, true),
    POINT(15, true),
    CROW(16, false),
    REST(19, true),
    CHAMP(21, false),
    AURA(22, false),
    BAT(23, false);

    private final @NonNegative int id;
    private final boolean isStatic;

    private static final Map<Integer, Emote> INDEX = Arrays.stream(values())
            .collect(Collectors.toMap(Emote::id, Function.identity()));

    Emote(@NonNegative int id, boolean isStatic) {
        this.id = id;
        this.isStatic = isStatic;
    }

    /**
     * Get the emote ID
     */
    public @NonNegative int id() {
        return id;
    }

    /**
     * Get the bitmask value for the emote list
     */
    public @NonNegative long bitmask() {
        return (@NonNegative long) (id > 0 ? 1L << (id - 1) : 0L);
    }

    /**
     * Is the emote static (persists until movement)
     */
    public boolean isStatic() {
        return isStatic;
    }

    /**
     * Check if the emote boosts life regeneration
     */
    public boolean boostsLifeRegeneration() {
        return this == SIT || this == REST;
    }

    /**
     * Get an emote from its ID
     */
    public static Emote fromId(int id) {
        return INDEX.getOrDefault(id, NONE);
    }
}
