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

import fr.quatrevieux.araknemu.game.admin.Command;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Format the help page of a command
 */
public final class HelpFormatter {
    private static class LinkItem {
        private final String command;
        private final String description;
        private final Link.Type linkType;

        public LinkItem(String command, String description, Link.Type linkType) {
            this.command = command;
            this.description = description;
            this.linkType = linkType;
        }

        public int leftLength() {
            return command.length();
        }

        public void build(OutputBuilder builder, int leftLength) {
            builder.indent(linkType.create(command));

            if (description != null) {
                if (leftLength < 32) {
                    builder.append(StringUtils.repeat(' ', leftLength - command.length()));
                }

                builder.append(" - ").append(description);
            }
        }
    }

    private final Command command;
    private String synopsis;
    private final List<Pair<String, String>> options = new ArrayList<>();
    private final List<LinkItem> examples = new ArrayList<>();
    private final List<LinkItem> seeAlso = new ArrayList<>();
    private final List<String> custom = new ArrayList<>();

    public HelpFormatter(Command command) {
        this.command = command;
        this.synopsis = command.name();
    }

    /**
     * Define the command synopsis
     * The synopsis define the options and arguments order
     *
     * The options and arguments are surrounded by brackets "[option]", and should be described using {@link HelpFormatter#options(String, String)}
     * A default option can be defined using format "[option=default value]"
     */
    public HelpFormatter synopsis(String synopsis) {
        this.synopsis = synopsis;

        return this;
    }

    /**
     * Describe an option or parameter
     *
     * @param option The option name
     * @param description The option description. May be multiple lines.
     */
    public HelpFormatter options(String option, String description) {
        options.add(new ImmutablePair<>(option, description));

        return this;
    }

    /**
     * Define an usage example
     * Multiple examples may be added
     *
     * @param example The command
     * @param description Example description : what the command should do ?
     */
    public HelpFormatter example(String example, String description) {
        examples.add(new LinkItem(example, description, Link.Type.WRITE));

        return this;
    }

    /**
     * Define a "see also" command, with an help link
     *
     * @param command The related command
     * @param description Describe the relation
     */
    public HelpFormatter seeAlso(String command, String description) {
        return seeAlso(command, description, Link.Type.HELP);
    }

    /**
     * Define a "see also" command, with custom link type
     */
    public HelpFormatter seeAlso(String command, String description, Link.Type linkType) {
        seeAlso.add(new LinkItem(command, description, linkType));

        return this;
    }

    /**
     * Add custom lines on the help
     */
    public HelpFormatter line(String... lines) {
        for (String line : lines) {
            custom.add(line);
        }

        return this;
    }

    /**
     * Render the help page
     */
    @Override
    public String toString() {
        final OutputBuilder builder = new OutputBuilder();

        buildHeader(builder);
        buildSynopsis(builder);
        buildOptions(builder);
        buildCustom(builder);
        buildLinkItems(builder, "EXAMPLES", examples);
        buildLinkItems(builder, "SEE ALSO", seeAlso);
        buildPermissions(builder);

        return builder.toString();
    }

    /**
     * Build the help header, with command name and description
     */
    private void buildHeader(OutputBuilder builder) {
        builder
            .append(command.name(), " - ", command.description())
            .line("========================================")
        ;
    }

    /**
     * Build the command synopsis section
     */
    private void buildSynopsis(OutputBuilder builder) {
        builder.title("SYNOPSIS").indent(synopsis);
    }

    /**
     * Build the options section
     */
    private void buildOptions(OutputBuilder builder) {
        if (options.isEmpty()) {
            return;
        }

        builder.title("OPTIONS");

        for (Pair<String, String> line : options) {
            builder.indent(line.getLeft());

            if (line.getRight() != null) {
                builder.append(" : ").append(line.getRight().replace("\n", "\n\t\t"));
            }
        }
    }

    /**
     * Build see also and example sections
     */
    private void buildLinkItems(OutputBuilder builder, String title, List<LinkItem> items) {
        if (items.isEmpty()) {
            return;
        }

        builder.title(title);

        final int leftLength = items.stream().mapToInt(LinkItem::leftLength).max().getAsInt();

        for (LinkItem item : items) {
            item.build(builder, leftLength);
        }
    }

    /**
     * Render the custom lines
     */
    private void buildCustom(OutputBuilder builder) {
        custom.forEach(builder::line);
    }

    /**
     * Build the permissions section
     */
    private void buildPermissions(OutputBuilder builder) {
        builder.title("PERMISSIONS").indent(command.permissions());
    }
}
