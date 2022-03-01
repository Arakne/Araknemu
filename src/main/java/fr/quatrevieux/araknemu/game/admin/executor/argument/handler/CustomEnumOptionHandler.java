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

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.util.NullnessUtil;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

import java.util.Arrays;

/**
 * Handle enum value
 * The enum is checked by its name, ignoring the case and "-" or "_" chars
 *
 * Unlike the default enum option handler, display as meta var available values in lowercase and only if there is at most 4 available values
 *
 * @param <E> The enum type
 */
public final class CustomEnumOptionHandler<E extends Enum<E>> extends OptionHandler<E> {
    public CustomEnumOptionHandler(CmdLineParser parser, OptionDef option, Setter<? super E> setter) {
        super(parser, option, setter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public int parseArguments(Parameters params) throws CmdLineException {
        final String value = params.getParameter(0).replace("_", "-");

        for (E item : (E[]) NullnessUtil.castNonNull(setter.getType().getEnumConstants())) {
            if (item.name().replace("_", "-").equalsIgnoreCase(value)) {
                setter.addValue(item);
                return 1;
            }
        }

        throw new CmdLineException(owner, "Invalid value " + value + " for " + optionName(params) + ". Available values : " + Arrays.toString(availableValues()));
    }

    @Override
    public String getDefaultMetaVariable() {
        // @todo test metavar
        final String[] values = availableValues();

        if (values.length <= 4) {
            return StringUtils.join(values, "|");
        }

        return setter.getType().getSimpleName();
    }

    @SuppressWarnings("unchecked")
    private String[] availableValues() {
        final E[] items = (E[]) NullnessUtil.castNonNull(setter.getType().getEnumConstants());
        final String[] names = new String[items.length];

        for (int i = 0; i < items.length; ++i) {
            names[i] = items[i].name().toLowerCase().replace("_", "-");
        }

        return names;
    }

    private String optionName(Parameters params) throws CmdLineException {
        if (!option.isArgument()) {
            return "option \"" + params.getParameter(-1) + "\"";
        }

        return "argument \"" + option + "\"";
    }
}
