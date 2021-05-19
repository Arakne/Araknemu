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

import fr.quatrevieux.araknemu.game.admin.formatter.OutputBuilder;

import java.util.List;
import java.util.Map;

public final class DefaultHelpRenderer implements HelpRenderer {
    @Override
    public void build(OutputBuilder builder, CommandHelp help) {
        builder.withAll(help.variables());

        buildHeader(builder, help);
        buildSynopsis(builder, help);
        buildOptions(builder, help);
        buildCustom(builder, help);
        buildLinkItems(builder, "EXAMPLES", help.examples());
        buildLinkItems(builder, "SEE ALSO", help.seeAlso());
        buildPermissions(builder, help);
    }

    /**
     * Build the help header, with command name and description
     */
    private void buildHeader(OutputBuilder builder, CommandHelp help) {
        builder
            .append(help.command().name(), " - ", help.description())
            .line("========================================")
        ;
    }

    /**
     * Build the command synopsis section
     */
    private void buildSynopsis(OutputBuilder builder, CommandHelp help) {
        builder.title("SYNOPSIS").indent(help.synopsis());
    }

    /**
     * Build the options section
     */
    private void buildOptions(OutputBuilder builder, CommandHelp help) {
        if (help.options().isEmpty()) {
            return;
        }

        builder.title("OPTIONS");

        for (Map.Entry<String, String> line : help.options().entrySet()) {
            builder.indent(line.getKey());

            if (line.getValue() != null) {
                builder.append(" : ").append(line.getValue().replace("\n", "\n\t\t"));
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
    private void buildCustom(OutputBuilder builder, CommandHelp help) {
        help.custom().forEach(builder::line);
    }

    /**
     * Build the permissions section
     */
    private void buildPermissions(OutputBuilder builder, CommandHelp help) {
        builder.title("PERMISSIONS").indent(help.command().permissions());
    }
}
