package fr.quatrevieux.araknemu.game.account.generator;

/**
 * Camelize generated name
 * Set to upper the first letter, and letter following an hyphen
 */
final public class CamelizeName implements NameGenerator {
    final private NameGenerator generator;

    public CamelizeName(NameGenerator generator) {
        this.generator = generator;
    }

    @Override
    public String generate() throws NameGenerationException {
        String generated = generator.generate();

        StringBuilder sb = new StringBuilder(generated.length());

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
