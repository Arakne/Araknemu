package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.network.in.SessionCreated;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StartSessionTest extends GameBaseCase {
    private StartSession handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new StartSession();
    }

    @Test
    void handle() {
        handler.handle(session, new SessionCreated());

        requestStack.assertLast("HG");
    }
}