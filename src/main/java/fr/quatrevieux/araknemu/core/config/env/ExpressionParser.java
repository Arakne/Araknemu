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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.core.config.env;

import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Parse and evaluate string interpolation expressions
 *
 * Handle following syntax:
 * - "$VAR" => value of VAR
 * - "${VAR}" => same as above
 * - "${VAR:default}" => value of VAR or "default" if VAR is not defined
 * - "${VAR:-default}" => same as above
 * - "Hello ${NAME:World} !" => "Hello World !" if NAME is not defined, "Hello John !" if NAME is "John"
 * - "$FOO$BAR" => value of FOO followed by value of BAR
 * - "\$FOO" => "$FOO" (escape the $)
 *
 * It does not handle nested expressions (use variable as default value is not supported)
 *
 * Note: spaces are not ignored, so "${ VAR : default }" will try to get the value of " VAR " (with spaces),
 *       and if not found, return " default "
 */
public final class ExpressionParser {
    private ExpressionParser() {
        // Disable instantiation
    }

    /**
     * Evaluate the expression
     *
     * @param value the expression to evaluate
     * @param context the context to get the value of variables
     *
     * @return the evaluated expression, with all variables replaced by their value
     */
    @SuppressWarnings("checkstyle:ModifiedControlVariable")
    public static String evaluate(String value, Context context) {
        // Value do not contain any interpolation
        if (!value.contains("$")) {
            return value;
        }

        value = cleanQuoteAndSpace(value);

        final StringBuilder sb = new StringBuilder(value.length());
        final int len = value.length();

        boolean escaped = false;
        int index = 0;

        while (index < len) {
            final char c = value.charAt(index++);

            if (escaped) {
                sb.append(c);
                escaped = false;
                continue;
            }

            if (c == '\\') {
                escaped = true;
                continue;
            }

            if (c == '$') {
                index = parseVariable(sb, value, index, context);
                continue;
            }

            sb.append(c);
        }

        return sb.toString();
    }

    private static String cleanQuoteAndSpace(String value) {
        value = value.trim();

        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }

        return value;
    }

    /**
     * Parse a variable expression
     *
     * @param output the output buffer
     * @param value the expression to parse
     * @param index the current index
     * @param context the context to get the value of variables
     *
     * @return The new index position
     */
    private static @NonNegative int parseVariable(StringBuilder output, String value, @NonNegative int index, Context context) {
        final int len = value.length();

        if (index >= len) {
            output.append('$');
            return index;
        }

        if (value.charAt(index) == '{') {
            return parseBraceVariable(output, value, index + 1, context);
        }

        final StringBuilder varName = new StringBuilder();

        for (; index < len; ++index) {
            final char c = value.charAt(index);

            if (!Character.isLetterOrDigit(c) && c != '_') {
                break; // End parsing the variable
            }

            varName.append(c);
        }

        output.append(context.get(varName.toString(), ""));

        return index;
    }

    /**
     * Parse a variable expression inside braces
     *
     * @param output the output buffer
     * @param value the expression to parse
     * @param index the current index
     * @param context the context to get the value of variables
     *
     * @return The new index position
     */
    private static @NonNegative int parseBraceVariable(StringBuilder output, String value, @NonNegative int index, Context context) {
        final int len = value.length();
        final StringBuilder varName = new StringBuilder();

        StringBuilder defaultValue = null;

        for (; index < len; ++index) {
            final char c = value.charAt(index);

            if (c == '}') {
                break; // End parsing the variable
            }

            if (defaultValue != null) {
                // Handle :- syntax
                if (defaultValue.length() == 0 && c == '-') {
                    continue;
                }

                defaultValue.append(c);
                continue;
            }

            if (c == ':') {
                defaultValue = new StringBuilder();
                continue;
            }

            varName.append(c);
        }

        final String resolvedDefaultValue = defaultValue != null ? defaultValue.toString() : "";
        output.append(context.get(varName.toString(), resolvedDefaultValue));

        return index + 1; // Skip the closing brace
    }

    @FunctionalInterface
    public interface Context {
        /**
         * Get the value of a variable.
         * If the variable is not defined, use the default value
         *
         * @param varName variable name
         * @param defaultValue default value
         *
         * @return the value of the variable or the default value
         */
        public String get(String varName, String defaultValue);
    }
}
