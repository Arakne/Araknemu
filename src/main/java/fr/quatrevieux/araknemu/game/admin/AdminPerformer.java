package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;

import java.util.Optional;
import java.util.Set;

/**
 * Perform admin operations
 */
public interface AdminPerformer extends AdminLogger {
    /**
     * Parse and execute a command
     *
     * @param command The command line
     *
     * @throws AdminException When an error occurs during executing the command
     */
    public void execute(String command) throws AdminException;

    /**
     * Check if the admin user has permissions
     *
     * @param permissions Set of required permissions
     */
    public boolean isGranted(Set<Permission> permissions);

    /**
     * Get the related account if exists
     */
    public Optional<GameAccount> account();
}
