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

package fr.quatrevieux.araknemu.game.admin.formatter;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Build and format the console output
 */
public final class OutputBuilder {
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{([a-z0-9_.-]+?)\\}\\}", Pattern.CASE_INSENSITIVE);

    private final StringBuilder builder;
    private final Map<String, Supplier<String>> variables = new HashMap<>();

    public OutputBuilder() {
        this.builder = new StringBuilder();
    }

    /**
     * Register a new variable for the output
     *
     * Variable are used with {{varname}} format
     *
     * Note: Variable are processed by the append method, not on the toString method
     *       So change the variable value after calling append will not change the generated value
     *
     * Usage:
     * <pre>{@code
     * builder
     *     .with("name", () -> "John")
     *     .append("Hello {{name}} !")
     *     .toString() // "Hello John !"
     * ;
     * }</pre>
     *
     * @param varName The variable name. Should be alpha num + "_" "-" and "."
     * @param variable The value generator
     *
     * @return this
     */
    public OutputBuilder with(String varName, Supplier<String> variable) {
        variables.put(varName, variable);

        return this;
    }

    /**
     * Declare multiple variable
     *
     * @param variables Map of variable, with name as key and supplier as value
     *
     * @return this
     *
     * @see OutputBuilder#with(String, Supplier) For variable usage
     */
    public OutputBuilder withAll(Map<String, Supplier<String>> variables) {
        this.variables.putAll(variables);

        return this;
    }

    /**
     * Append a simple stringable value
     */
    public OutputBuilder append(Object value) {
        builder.append(value);

        return this;
    }

    /**
     * Append a text
     */
    public OutputBuilder append(String text) {
        builder.append(processVariables(text));

        return this;
    }

    /**
     * Append multiple text
     */
    public OutputBuilder append(String... text) {
        for (String s : text) {
            builder.append(processVariables(s));
        }

        return this;
    }

    /**
     * Render a new line
     */
    public OutputBuilder line(String line) {
        builder.append('\n').append(processVariables(line));

        return this;
    }

    /**
     * Render a section title
     */
    public OutputBuilder title(String title) {
        builder.append("\n\n<b>").append(processVariables(title)).append("</b>");

        return this;
    }

    /**
     * Render an indented line
     */
    public OutputBuilder indent(Object value) {
        return indent(value.toString());
    }

    /**
     * Render an indented line
     */
    public OutputBuilder indent(String text) {
        return indent(text, 1);
    }

    /**
     * Render an indented line
     */
    public OutputBuilder indent(String text, int count) {
        final String indent = "\n" + StringUtils.repeat('\t', count);

        builder.append(indent).append(processVariables(text).replace("\n", indent));

        return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    private String processVariables(String str) {
        if (variables.isEmpty()) {
            return str;
        }

        final StringBuffer out = new StringBuffer();
        final Matcher matcher = VARIABLE_PATTERN.matcher(str);

        while (matcher.find()) {
            final MatchResult result = matcher.toMatchResult();
            final String varName = result.group(1);

            if (!variables.containsKey(varName)) {
                continue;
            }

            matcher.appendReplacement(out, variables.get(varName).get());
        }

        matcher.appendTail(out);

        return out.toString();
    }
}
