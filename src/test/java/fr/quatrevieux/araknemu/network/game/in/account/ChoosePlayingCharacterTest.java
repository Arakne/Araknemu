package fr.quatrevieux.araknemu.network.game.in.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChoosePlayingCharacterTest {
    @Test
    void parse() {
        assertEquals(123, new ChoosePlayingCharacter.Parser().parse("123").id());
    }
}
