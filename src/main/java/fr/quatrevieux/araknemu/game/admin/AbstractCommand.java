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

package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.common.account.Permission;
import org.apache.commons.lang3.StringUtils;

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
        public Builder help(String... help) {
            AbstractCommand.this.help = StringUtils.join(help, "\n");

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
