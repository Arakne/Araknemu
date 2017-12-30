package fr.quatrevieux.araknemu.network.game.out.game;

import fr.quatrevieux.araknemu.network.game.in.game.CreateGameRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameCreatedTest {
    @Test
    void generate() {
        assertEquals("GCK|1", new GameCreated(CreateGameRequest.Type.EXPLORATION).toString());
    }
}