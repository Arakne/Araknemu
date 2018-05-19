package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.network.game.in.QuickPing;
import fr.quatrevieux.araknemu.network.game.out.QuickPong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SendQuickPongTest extends GameBaseCase {
    private SendQuickPong handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new SendQuickPong();
    }

    @Test
    void ping() {
        handler.handle(session, new QuickPing());

        requestStack.assertLast(new QuickPong());
    }
}