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
import fr.quatrevieux.araknemu.game.admin.formatter.HelpFormatter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Base command class
 */
public abstract class AbstractCommand<A> implements Command<A> {
    private final HelpFormatter help = new HelpFormatter(this);
    private final EnumSet<Permission> permissions = EnumSet.of(Permission.ACCESS);
    private String description = "No description";
    private boolean initialized = false;

    /**
     * Build the command
     */
    protected abstract void build(Builder builder);

    @Override
    public final String description() {
        initialize();

        return description;
    }

    @Override
    public final String help() {
        initialize();

        return help.toString();
    }

    @Override
    public final Set<Permission> permissions() {
        initialize();

        return permissions;
    }

    private void initialize() {
        if (!initialized) {
            build(new Builder());
            initialized = true;
        }
    }

    protected final class Builder {
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
}
