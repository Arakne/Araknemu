package fr.quatrevieux.araknemu.network.game.out.basic.admin;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConsolePromptTest {
    @Test
    void generate() {
        assertEquals("BAPTest", new ConsolePrompt("Test").toString());
    }
}
