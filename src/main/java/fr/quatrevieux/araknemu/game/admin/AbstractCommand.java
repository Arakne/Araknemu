package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.common.account.Permission;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * Base command class
 */
abstract public class AbstractCommand implements Command {
    final protected class Builder {
        /**
         * Set a command description
         */
        public Builder description(String description) {
            AbstractCommand.this.description = description;

            return this;
        }

        /**
         * Set a command help message
         */
        public Builder help(String help) {
            AbstractCommand.this.help = help;

            return this;
        }

        /**
         * Add required permissions
         */
        public Builder requires(Permission... permissions) {
            AbstractCommand.this.permissions.addAll(Arrays.asList(permissions));

            return this;
        }
    }

    private String description = "No description";
    private String help = "No help";
    private EnumSet<Permission> permissions = EnumSet.of(Permission.ACCESS);

    public AbstractCommand() {
        build(new Builder());
    }

    /**
     * Build the command
     */
    abstract protected void build(Builder builder);

    @Override
    final public String description() {
        return description;
    }

    @Override
    final public String help() {
        return
            name() + " - " + description + '\n' +
            "========================================\n\n" +
            help
        ;
    }

    @Override
    final public Set<Permission> permissions() {
        return permissions;
    }
}
