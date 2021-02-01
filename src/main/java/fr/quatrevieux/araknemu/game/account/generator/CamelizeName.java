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

package fr.quatrevieux.araknemu.game.account.generator;

/**
 * Camelize generated name
 * Set to upper the first letter, and letter following an hyphen
 */
public final class CamelizeName implements NameGenerator {
    private final NameGenerator generator;

    public CamelizeName(NameGenerator generator) {
        this.generator = generator;
    }

    @Override
    public String generate() throws NameGenerationException {
        final String generated = generator.generate();
        final StringBuilder sb = new StringBuilder(generated.length());

        for (int i = 0; i < generated.length(); ++i) {
            char c = generated.charAt(i);

            if (i == 0 || generated.charAt(i - 1) == '-') {
                c = Character.toUpperCase(c);
            }

            sb.append(c);
        }

        return sb.toString();
    }
}
