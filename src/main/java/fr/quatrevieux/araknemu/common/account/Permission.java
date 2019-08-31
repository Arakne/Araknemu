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

package fr.quatrevieux.araknemu.common.account;

/**
 * List of admin permissions
 */
public enum Permission {
    ACCESS,
    SUPER_ADMIN,
    MANAGE_PLAYER,
    MANAGE_ACCOUNT,
    DEBUG;

    /**
     * Get the permission bit
     */
    public int id() {
        return 1 << ordinal();
    }

    /**
     * Check if the permission match with the given value
     */
    public boolean match(int value) {
        return (value & id()) == id();
    }
}
