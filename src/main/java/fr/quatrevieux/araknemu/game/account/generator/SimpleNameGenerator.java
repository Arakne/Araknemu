package fr.quatrevieux.araknemu.game.account.generator;

import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.util.RandomUtil;

/**
 * Simple generator for character names switching between consonants and vowels
 */
final public class SimpleNameGenerator implements NameGenerator {
    final static private char[] CONSONANTS = new char[] {'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'z'};
    final static private char[] VOWELS     = new char[] {'a', 'e', 'i', 'o', 'u', 'y'};

    final private GameConfiguration.PlayerConfiguration configuration;
    final private RandomUtil random;

    public SimpleNameGenerator(GameConfiguration.PlayerConfiguration configuration) {
        this.configuration = configuration;
        this.random = new RandomUtil();
    }

    @Override
    public String generate() {
        int length = random.rand(configuration.minNameGeneratedLength(), configuration.maxNameGeneratedLength());

        StringBuilder sb = new StringBuilder(length);

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
