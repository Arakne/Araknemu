package fr.quatrevieux.araknemu.game.admin.exception;

import fr.quatrevieux.araknemu.common.account.Permission;

import java.util.Set;

/**
 * Required permissions is not granted for execute the command
 */
public class CommandPermissionsException extends CommandException {
    final private Set<Permission> permissions;

    public CommandPermissionsException(String command, Set<Permission> permissions) {
        super(command);
        this.permissions = permissions;
    }

    public Set<Permission> permissions() {
        return permissions;
    }
}
