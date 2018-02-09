package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;

import java.util.List;
import java.util.Set;

/**
 * A console command
 */
public interface Command {
    /**
     * Get the command name
     */
    public String name();

    /**
     * Get a simple description for the command
     */
    public String description();

    /**
     * Get the help message
     */
    public String help();

    /**
     * Execute the command
     *
     * @param performer The command performer
     * @param arguments The command arguments
     */
    public void execute(AdminPerformer performer, List<String> arguments) throws AdminException;

    /**
     * Get list of required permissions
     */
    public Set<Permission> permissions();
}
