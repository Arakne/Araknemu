package fr.quatrevieux.araknemu.data.living.transformer;

import fr.quatrevieux.araknemu.common.account.Permission;

import java.util.EnumSet;
import java.util.Set;

/**
 * Transformer for permissions set
 */
final public class PermissionsTransformer {
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

    public Set<Permission> unserialize(int serialized) {
        Set<Permission> permissions = EnumSet.noneOf(Permission.class);

        for (Permission permission : Permission.values()) {
            if (permission.match(serialized)) {
                permissions.add(permission);
            }
        }

        return permissions;
    }
}
