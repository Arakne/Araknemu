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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.formatter.HelpFormatter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

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
         * Configure the help page
         *
         * <code>
         *     builder.help(
         *         formatter -> formatter
         *             .synopsis("my_command [option]")
         *             .options("--opt", "opt description")
         *             .example("my_command --opt", "example description")
         *     );
         * </code>
         */
        public Builder help(Consumer<HelpFormatter> configurator) {
            configurator.accept(help);

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

    final private HelpFormatter help = new HelpFormatter(this);
    final private EnumSet<Permission> permissions = EnumSet.of(Permission.ACCESS);
    private String description = "No description";
    private boolean initialized = false;

    /**
     * Build the command
     */
    abstract protected void build(Builder builder);

    @Override
    final public String description() {
        initialize();

        return description;
    }

    @Override
    final public String help() {
        initialize();

        return help.toString();
    }

    @Override
    final public Set<Permission> permissions() {
        initialize();

        return permissions;
    }

    @Override
    public void execute(AdminPerformer performer, CommandParser.Arguments arguments) throws AdminException {
        execute(performer, arguments.arguments());
    }

    /**
     * Adapt the new Command interface to the legacy one
     */
    public void execute(AdminPerformer performer, List<String> arguments) throws AdminException {
        throw new AdminException("Not implemented");
    }

    private void initialize() {
        if (!initialized) {
            build(new Builder());
            initialized = true;
        }
    }
}
