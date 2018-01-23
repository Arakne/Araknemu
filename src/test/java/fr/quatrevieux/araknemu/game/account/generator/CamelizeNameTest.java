package fr.quatrevieux.araknemu.game.account.generator;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class CamelizeNameTest {
    @Test
    void generateSimple() throws NameGenerationException {
        NameGenerator generator = Mockito.mock(NameGenerator.class);
        CamelizeName camelizeName = new CamelizeName(generator);

        Mockito.when(generator.generate()).thenReturn("mysimplename");

        assertEquals("Mysimplename", camelizeName.generate());
    }

    @Test
    void generateWithHyphen() throws NameGenerationException {
        NameGenerator generator = Mockito.mock(NameGenerator.class);
        CamelizeName camelizeName = new CamelizeName(generator);

        Mockito.when(generator.generate()).thenReturn("my-complex-name");

        assertEquals("My-Complex-Name", camelizeName.generate());
    }
}
