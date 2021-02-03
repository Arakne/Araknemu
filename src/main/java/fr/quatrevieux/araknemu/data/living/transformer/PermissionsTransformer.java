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

package fr.quatrevieux.araknemu.data.living.transformer;

import fr.quatrevieux.araknemu.common.account.Permission;

import java.util.EnumSet;
import java.util.Set;

/**
 * Transformer for permissions set
 */
public final class PermissionsTransformer {
    /**
     * Get the bitfield values of the permissions set
     *
     * @param value The permission set
     *
     * @return Integer consists of a bitfield of all permissions
     */
    public int serialize(Set<Permission> value) {
        if (value == null)  {
            return 0;
        }

        int ret = 0;

        for (Permission permission : value) {
            ret |= permission.id();
        }

        return ret;
    }

    /**
     * Parse a bitfield to get a set of permissions
     *
     * @param serialized The bitfield
     *
     * @return The set of permissions
     */
    public Set<Permission> unserialize(int serialized) {
        final Set<Permission> permissions = EnumSet.noneOf(Permission.class);

        for (Permission permission : Permission.values()) {
            if (permission.match(serialized)) {
                permissions.add(permission);
            }
        }

        return permissions;
    }
}
