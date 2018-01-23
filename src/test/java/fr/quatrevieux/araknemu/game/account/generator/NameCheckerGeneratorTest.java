package fr.quatrevieux.araknemu.game.account.generator;

import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class NameCheckerGeneratorTest extends GameBaseCase {
    private NameCheckerGenerator nameCheckerGenerator;
    private NameGenerator inner;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushPlayer("Bob", 2, 2);
        dataSet.pushPlayer("Robert", 2, 1);
        dataSet.pushPlayer("Albert", 2, 2);

        inner = Mockito.mock(NameGenerator.class);
        nameCheckerGenerator = new NameCheckerGenerator(
            inner,
            container.get(PlayerRepository.class),
            container.get(GameConfiguration.class)
        );
    }

    @Test
    void cannotGenerateNameAlreadyUsed() throws NameGenerationException {
        Mockito.when(inner.generate()).thenReturn("Bob");

        assertThrows(NameGenerationException.class, () -> nameCheckerGenerator.generate(), "Reach the maximum try number");
    }

    @Test
    void generateFreeName() throws NameGenerationException {
        Mockito.when(inner.generate()).thenReturn("Jean");

        assertEquals("Jean", nameCheckerGenerator.generate());
    }

    @Test
    void generateWith2Tries() throws NameGenerationException {
        Mockito.when(inner.generate()).thenReturn("Bob", "Robert");

        assertEquals("Robert", nameCheckerGenerator.generate());
    }
}
