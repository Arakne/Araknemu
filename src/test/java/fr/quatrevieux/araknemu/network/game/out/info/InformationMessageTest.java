package fr.quatrevieux.araknemu.network.game.out.info;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InformationMessageTest {
    private static class Impl extends InformationMessage {
        public Impl(Type type, Entry... entries) {
            super(type, entries);
        }
    }

    @Test
    void simple() {
        assertEquals(
            "Im012;",
            new Impl(InformationMessage.Type.INFO, new InformationMessage.Entry(12)).toString()
        );
    }

    @Test
    void multiple() {
        assertEquals(
            "Im05;|6;|7;",
            new Impl(
                InformationMessage.Type.INFO,
                new InformationMessage.Entry(5),
                new InformationMessage.Entry(6),
                new InformationMessage.Entry(7)
            ).toString()
        );
    }

    @Test
    void withArguments() {
        assertEquals(
            "Im05;Hello~45~World",
            new Impl(
                InformationMessage.Type.INFO,
                new InformationMessage.Entry(5, "Hello", 45, "World")
            ).toString()
        );
    }
}