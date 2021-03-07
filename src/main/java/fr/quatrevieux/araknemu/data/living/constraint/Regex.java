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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.data.living.constraint;

import java.util.regex.Pattern;

/**
 * Perform a regex value on the value
 *
 * @param <T> The entity type
 * @param <E> The error type
 */
public final class Regex<T, E> extends AbstractValueConstraint<T, E, String> {
    private final Pattern regex;

    public Regex(E error, Getter<T, String> getter, Pattern regex) {
        super(error, getter);

        this.regex = regex;
    }

    public Regex(E error, Getter<T, String> getter, String regex) {
        this(error, getter, Pattern.compile(regex));
    }

    @Override
    protected boolean checkValue(String value) {
        return regex.matcher(value).matches();
    }
}
