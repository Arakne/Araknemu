package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.network.game.in.Ping;
import fr.quatrevieux.araknemu.network.game.out.Pong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SendPongTest extends GameBaseCase {
    private SendPong handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new SendPong();
    }

    @Test
    void handle() throws Exception {
        handler.handle(session, new Ping());

        requestStack.assertLast(new Pong());
    }
}