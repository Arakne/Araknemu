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
    private static final String[] STARTS = new String[] {"b","br","cr","d","dr","f","fr","g","gr","k","kr","l","m","n","p","pr","r","s","sh","t","tr","v","z","x"};
    private static final String[] VOWELS = new String[] {"a","e","i","o","u","y","ae","ia","ou","ei"};
    private static final String[] ENDS = new String[] {"n","r","s","k","l","th","ra","dor","mir","na","lia", "os","as","en","is","or","yn","ok","ar"};

    private final GameConfiguration.PlayerConfiguration configuration;
    private final RandomUtil random;

    public SimpleNameGenerator(GameConfiguration.PlayerConfiguration configuration) {
        this.configuration = configuration;
        this.random = new RandomUtil();
    }

    @Override
    public String generate() {
        final int syllables = random.rand(2, 4);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < syllables; i++) {
            sb.append(randomStart());
            sb.append(randomVowel());

            // 60% de chance d'ajouter une fin
            if (random.rand(1, 100) <= 60) {
                sb.append(randomEnds());
            }
        }

        String name = sb.toString();
        if (name.length() > configuration.maxNameGeneratedLength() || name.length() < configuration.minNameGeneratedLength()) {
            return generate();
        }

        return name;
    }

    private String randomStart() {
        return random.of(STARTS);
    }

    private String randomVowel() {
        return random.of(VOWELS);
    }

    private String randomEnds() {
        return random.of(ENDS);
    }
}
