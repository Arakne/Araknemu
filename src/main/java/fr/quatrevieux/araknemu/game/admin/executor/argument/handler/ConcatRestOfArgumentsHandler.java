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
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

/**
 * Contact all arguments into a single {@link String} separated with single space " "
 * Unlike {@link org.kohsuke.args4j.spi.RestOfArgumentsHandler} this handler handle a simple string
 */
public final class ConcatRestOfArgumentsHandler extends OptionHandler<String> {
    public ConcatRestOfArgumentsHandler(CmdLineParser parser, OptionDef option, Setter<? super String> setter) {
        super(parser, option, setter);
    }

    @Override
    public int parseArguments(Parameters params) throws CmdLineException {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); ++i) {
            if (i > 0) {
                sb.append(' ');
            }

            sb.append(params.getParameter(i));
        }

        setter.addValue(sb.toString());

        return params.size();
    }

    @Override
    public String getDefaultMetaVariable() {
        return "TEXT";
    }
}
