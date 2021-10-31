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

import fr.quatrevieux.araknemu.game.admin.formatter.Link;
import fr.quatrevieux.araknemu.game.admin.formatter.OutputBuilder;
import org.apache.commons.lang3.StringUtils;

final class LinkItem {
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
