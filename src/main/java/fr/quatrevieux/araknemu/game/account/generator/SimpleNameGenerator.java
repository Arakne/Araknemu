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

package fr.quatrevieux.araknemu.game.account.generator;

import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.game.GameConfiguration;

/**
 * Simple generator for character names switching between consonants and vowels
 */
public final class SimpleNameGenerator implements NameGenerator {
    private static final char[] CONSONANTS = new char[] {'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'z'};
    private static final char[] VOWELS     = new char[] {'a', 'e', 'i', 'o', 'u', 'y'};

    private final GameConfiguration.PlayerConfiguration configuration;
    private final RandomUtil random;

    public SimpleNameGenerator(GameConfiguration.PlayerConfiguration configuration) {
        this.configuration = configuration;
        this.random = new RandomUtil();
    }

    @Override
    public String generate() {
        final int length = random.rand(configuration.minNameGeneratedLength(), configuration.maxNameGeneratedLength());
        final StringBuilder sb = new StringBuilder(length);

        boolean isVowel = random.bool();

        for (int i = 0; i < length; ++i) {
            sb.append(isVowel ? randomVowel() : randomConsonant());
            isVowel = !isVowel;
        }

        return sb.toString();
    }

    private char randomVowel() {
        return random.of(VOWELS);
    }

    private char randomConsonant() {
        return random.of(CONSONANTS);
    }
}
