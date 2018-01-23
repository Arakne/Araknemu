package fr.quatrevieux.araknemu.game.account.generator;

import fr.quatrevieux.araknemu.game.GameConfiguration;

import java.util.Random;

/**
 * Simple generator for character names switching between consonants and vowels
 */
final public class SimpleNameGenerator implements NameGenerator {
    final static private char[] CONSONANTS = new char[] {'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'z'};
    final static private char[] VOWELS     = new char[] {'a', 'e', 'i', 'o', 'u', 'y'};

    final private GameConfiguration.PlayerConfiguration configuration;
    final private Random random;

    public SimpleNameGenerator(GameConfiguration.PlayerConfiguration configuration) {
        this.configuration = configuration;
        this.random = new Random();
    }

    @Override
    public String generate() {
        int length = random.nextInt(configuration.maxNameGeneratedLength() - configuration.minNameGeneratedLength()) + configuration.minNameGeneratedLength();

        StringBuilder sb = new StringBuilder(length);

        boolean isVowel = random.nextBoolean();

        for (int i = 0; i < length; ++i) {
            sb.append(isVowel ? randomVowel() : randomConsonant());
            isVowel = !isVowel;
        }

        return sb.toString();
    }

    private char randomVowel() {
        return VOWELS[random.nextInt(VOWELS.length)];
    }

    private char randomConsonant() {
        return CONSONANTS[random.nextInt(CONSONANTS.length)];
    }
}
