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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin.help;

import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.formatter.Link;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Help page data for a command
 */
public final class CommandHelp {
    public static final String DEFAULT_DESCRIPTION = "No description";

    private final Command command;

    private String synopsis;
    private String description;
    private final Map<String, String> options;
    private final List<LinkItem> examples;
    private final List<LinkItem> seeAlso;
    private final List<String> custom;
    private final Map<String, Supplier<String>> variables;

    public CommandHelp(Command command) {
        this.command = command;

        this.options = new LinkedHashMap<>();
        this.examples = new ArrayList<>();
        this.seeAlso = new ArrayList<>();
        this.custom = new ArrayList<>();
        this.variables = new HashMap<>();
    }

    private CommandHelp(CommandHelp other) {
        this.command = other.command;
        this.synopsis = other.synopsis;
        this.description = other.description;

        this.options = new LinkedHashMap<>(other.options);
        this.examples = new ArrayList<>(other.examples);
        this.seeAlso = new ArrayList<>(other.seeAlso);
        this.custom = new ArrayList<>(other.custom);
        this.variables = new HashMap<>(other.variables);
    }

    /**
     * Get a simple description of the command
     * This description must be a single line
     */
    public String description() {
        return description == null ? DEFAULT_DESCRIPTION : description;
    }

    /**
     * Get the command synopsis
     *
     * The synopsis should contains the command name, arguments and options
     * Arguments are in upper case
     * Options stats with --
     * Option arguments or option are surrounded by []
     * Default option values are defined by the = sign
     *
     * Ex:
     * foo BAR [BAZ=42] [--opt] [--other N=-1]
     *
     * - foo is the command name
     * - BAR is the first required argument
     * - BAZ is the second option argument. Its default value is 42
     * - opt is an optional option which takes no value
     * - other is an option option which takes a number as value. Its default value is -1
     */
    public String synopsis() {
        return synopsis == null ? command.name() : synopsis;
    }

    /**
     * Get all available options, indexed by name
     * Like for {@link CommandHelp#synopsis()} arguments are in upper case, and option starts with --
     *
     * @return The map of options. The map is unmodifiable.
     */
    public Map<String, String> options() {
        return Collections.unmodifiableMap(options);
    }

    /**
     * Modify the help, and return the modified instance
     *
     * Note: This method do not modify the current instance, but creates a new one
     *
     * Usage:
     * <pre>{@code
     * help = help.modify(builder -> builder
     *     .synopsis("foo BAR [--opt]")
     *     .description("Simple foo command")
     *     .option("BAR", "Define bar")
     *     .option("--opt", "Enable opt")
     * );
     * }
     * }</pre>
     *
     * @return The new help instance
     */
    public CommandHelp modify(Consumer<Builder> configurator) {
        final Builder builder = new Builder(new CommandHelp(this));

        configurator.accept(builder);

        return builder.help;
    }

    /**
     * Render the help into a string
     */
    public String render(HelpRenderer renderer) {
        return renderer.render(this);
    }

    /**
     * Render the help page using the {@link DefaultHelpRenderer}
     */
    @Override
    public String toString() {
        return render(new DefaultHelpRenderer());
    }

    /**
     * @return The related command
     */
    Command command() {
        return command;
    }

    /**
     * @return List of examples. The list is unmodifiable.
     */
    List<LinkItem> examples() {
        return Collections.unmodifiableList(examples);
    }

    /**
     * @return List of "see also" links. The list is unmodifiable.
     */
    List<LinkItem> seeAlso() {
        return Collections.unmodifiableList(seeAlso);
    }

    /**
     * @return List of additional lines. The list is unmodifiable.
     */
    List<String> custom() {
        return Collections.unmodifiableList(custom);
    }

    /**
     * @return List of declared variable. The map is unmodifiable.
     */
    Map<String, Supplier<String>> variables() {
        return Collections.unmodifiableMap(variables);
    }

    public static final class Builder {
        private final CommandHelp help;

        public Builder(CommandHelp help) {
            this.help = help;
        }

        /**
         * Define the command description
         *
         * @see CommandHelp#description()
         */
        public Builder description(String description) {
            help.description = description;

            return this;
        }

        /**
         * Define the command synopsis
         * The synopsis define the options and arguments order
         *
         * The synopsis should contains the command name, arguments and options
         * Arguments are in upper case
         * Options stats with "--"
         * Option arguments or option are surrounded by brackets "[]"
         * Default option values are defined by the "=" sign
         *
         * Ex:
         * "foo BAR [BAZ=42] [--opt] [--other N=-1]"
         *
         * - foo is the command name
         * - BAR is the first required argument
         * - BAZ is the second option argument. Its default value is 42
         * - opt is an optional option which takes no value
         * - other is an option option which takes a number as value. Its default value is -1
         *
         * @see CommandHelp#synopsis()
         */
        public Builder synopsis(String synopsis) {
            help.synopsis = synopsis;

            return this;
        }

        /**
         * Check if the synopsis is already provided
         */
        public boolean hasSynopsis() {
            return help.synopsis != null;
        }

        /**
         * Describe an option or parameter
         *
         * The argument name should be in upper case
         * The option name should starts with "--"
         *
         * Usage:
         * <pre>{@code
         * builder
         *     .option("ARG", "My argument description")
         *     .option("--opt", "My option description")
         *     .option("--multiline", "The description\n" +
         *         "can be\n" +
         *         "on multiple lines"
         *     )
         * }</pre>
         *
         * @param option The option or argument name
         * @param description The option description. May be multiple lines.
         *
         * @see CommandHelp#options()
         */
        public Builder option(String option, String description) {
            help.options.put(option, description);

            return this;
        }

        /**
         * Check if an option (or argument) is already provided
         */
        public boolean hasOption(String name) {
            return help.options.containsKey(name);
        }

        /**
         * Define an usage example
         * Multiple examples may be added
         *
         * Usage:
         * <pre>{@code
         * builder
         *    .example("foo bar --opt", "My example description")
         *    .example("foo bar", "Example without opt")
         * }</pre>
         *
         * @param example The command
         * @param description Example description : what the command should do ?
         */
        public Builder example(String example, String description) {
            help.examples.add(new LinkItem(example, description, Link.Type.WRITE));

            return this;
        }

        /**
         * Define a "see also" command, with an help link
         *
         * @param command The related command
         * @param description Describe the relation
         */
        public Builder seeAlso(String command, String description) {
            return seeAlso(command, description, Link.Type.HELP);
        }

        /**
         * Define a "see also" command, with custom link type
         */
        public Builder seeAlso(String command, String description, Link.Type linkType) {
            help.seeAlso.add(new LinkItem(command, description, linkType));

            return this;
        }

        /**
         * Add custom lines on the help
         */
        public Builder line(String... lines) {
            help.custom.addAll(Arrays.asList(lines));

            return this;
        }

        /**
         * Register a new variable for the output
         *
         * Variable are used with {{varname}} format in any place
         * So you can use it to add dynamic value on annotation description
         *
         * Usage:
         * <pre>{@code
         * builder
         *     .option("--opt", "My option. Available values are : {{opt.values}}")
         *     .with("opt.values", () -> "My complex list of values")
         * ;
         * }</pre>
         *
         * @param varName The variable name. Should be alpha num + "_" "-" and "."
         * @param variable The value generator
         *
         * @return this
         */
        public Builder with(String varName, Supplier<String> variable) {
            help.variables.put(varName, variable);

            return this;
        }

        /**
         * Register a new constant value for the output
         *
         * Variable are used with {{varname}} format in any place
         *
         * @param varName The variable name. Should be alpha num + "_" "-" and "."
         * @param value The value
         *
         * @return this
         *
         * @see Builder#with(String, String)
         */
        public Builder with(String varName, String value) {
            return with(varName, () -> value);
        }

        /**
         * Register an enum variable for the output
         *
         * Variable are used with {{varname}} format in any place
         *
         * @param varName The variable name. Should be alpha num + "_" "-" and "."
         * @param value The value
         *
         * @return this
         *
         * @see Builder#with(String, String)
         */
        public Builder with(String varName, Class<? extends Enum> value) {
            return with(
                varName,
                () -> Arrays.stream(value.getEnumConstants()).map(Enum::name).collect(Collectors.joining(", "))
            );
        }
    }
}
