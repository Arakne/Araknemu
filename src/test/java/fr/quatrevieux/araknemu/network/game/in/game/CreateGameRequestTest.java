package fr.quatrevieux.araknemu.network.game.in.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateGameRequestTest {
    @Test
    void parse() {
        CreateGameRequest.Parser parser = new CreateGameRequest.Parser();

        assertEquals(CreateGameRequest.Type.EXPLORATION, parser.parse("1").type());
    }
}
