package fr.quatrevieux.araknemu.game.account.generator;

/**
 * Generator for character name
 */
public interface NameGenerator {
    /**
     * Generate a random name.
     * The generated name should be unique over all the server
     */
    public String generate() throws NameGenerationException;
}
