package fr.quatrevieux.araknemu.network.game.in.basic.admin;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminCommandTest {
    @Test
    void parse() {
        AdminCommand.Parser parser = new AdminCommand.Parser();

        assertEquals("cmd", parser.parse("cmd").command());
    }
}
