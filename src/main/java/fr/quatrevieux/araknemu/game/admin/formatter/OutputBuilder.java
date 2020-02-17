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

/**
 * Build and format the console output
 */
final public class OutputBuilder {
    final private StringBuilder builder;

    public OutputBuilder() {
        this.builder = new StringBuilder();
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
        builder.append(text);

        return this;
    }

    /**
     * Append multiple text
     */
    public OutputBuilder append(String... text) {
        for (String s : text) {
            builder.append(s);
        }

        return this;
    }

    /**
     * Render a new line
     */
    public OutputBuilder line(String line) {
        builder.append('\n').append(line);

        return this;
    }

    /**
     * Render a section title
     */
    public OutputBuilder title(String title) {
        builder.append("\n\n<b>").append(title).append("</b>");

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

        builder.append(indent).append(text.replace("\n", indent));

        return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
