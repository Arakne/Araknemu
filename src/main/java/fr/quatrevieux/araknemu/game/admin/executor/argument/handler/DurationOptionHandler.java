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

package fr.quatrevieux.araknemu.game.admin.executor.argument.handler;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OneArgumentOptionHandler;
import org.kohsuke.args4j.spi.Setter;

import java.time.DateTimeException;
import java.time.Duration;

/**
 * Option handler for parsing a {@link Duration}
 *
 * Use the standard duration format (ISO-8601) with some differences :
 * - The string is case insensitive
 * - The prefix "PT" is not required
 *
 * Ex: "15s"    => 15 seconds
 *     "2m5s"   => 2 minutes and 5 seconds
 *     "p1dt5h" => 1 day and 5 hours
 */
public final class DurationOptionHandler extends OneArgumentOptionHandler<Duration> {
    public DurationOptionHandler(CmdLineParser parser, OptionDef option, Setter<? super Duration> setter) {
        super(parser, option, setter);
    }

    @Override
    protected Duration parse(String argument) throws NumberFormatException, CmdLineException {
        String value = argument.toUpperCase();

        if (value.isEmpty()) {
            throw new NumberFormatException("Invalid duration");
        }

        if (value.charAt(0) != 'P') {
            if (!value.contains("T") && !value.contains("D")) {
                value = "PT" + value;
            } else {
                value = "P" + value;
            }
        }

        try {
            return Duration.parse(value);
        } catch (DateTimeException e) {
            throw new NumberFormatException("Invalid duration");
        }
    }
}
