package fr.quatrevieux.araknemu.game.account.generator;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleNameGeneratorTest extends GameBaseCase {
    private SimpleNameGenerator generator;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        generator = new SimpleNameGenerator(container.get(GameConfiguration.class).player());
    }

    @Test
    void generateLength() {
        assertTrue(generator.generate().length() >= 4);
        assertTrue(generator.generate().length() <= 8);
    }

    @Test
    void generateShouldNotBeSame() {
        assertNotEquals(
            generator.generate(),
            generator.generate()
        );
    }
}
