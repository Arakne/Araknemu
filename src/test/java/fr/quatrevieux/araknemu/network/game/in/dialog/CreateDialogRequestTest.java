package fr.quatrevieux.araknemu.network.game.in.dialog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateDialogRequestTest {
    @Test
    void parse() {
        assertEquals(-24704, new CreateDialogRequest.Parser().parse("-24704").npcId());
    }
}