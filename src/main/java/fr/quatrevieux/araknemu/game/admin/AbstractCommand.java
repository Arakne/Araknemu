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
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.admin.help.CommandHelp;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Base command class
 */
public abstract class AbstractCommand<A> implements Command<A> {
    private @SuppressWarnings({"argument", "assignment"}) CommandHelp help = new CommandHelp(this);
    private final EnumSet<Permission> permissions = EnumSet.of(Permission.ACCESS);
    private @Nullable Supplier<A> argumentsFactory;
    private boolean initialized = false;

    /**
     * Build the command
     */
    protected abstract void build(Builder builder);

    @Override
    public CommandHelp help() {
        initialize();

        return help;
    }

    @Override
    public final Set<Permission> permissions() {
        initialize();

        return permissions;
    }

    @Override
    public @Nullable A createArguments() {
        initialize();

        if (argumentsFactory != null) {
            return argumentsFactory.get();
        }

        return null;
    }

    /**
     * Raise a command error and stop execution of the command
     *
     * @param message The error message
     */
    protected final void error(String message) throws CommandException {
        throw new CommandException(name(), message);
    }

    private void initialize() {
        if (!initialized) {
            build(new Builder());
            initialized = true;
        }
    }

    protected final class Builder {
        /**
         * Define the command arguments factory
         *
         * Note: this method should be used only for annotated object argument.
         *       For other arguments type, prefer overrides the method {@link Command#createArguments()}
         */
        public Builder arguments(Supplier<A> constructor) {
            AbstractCommand.this.argumentsFactory = constructor;

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
        public Builder help(Consumer<CommandHelp.Builder> configurator) {
            help = help.modify(configurator);

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
