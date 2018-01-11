package fr.quatrevieux.araknemu.game.handler.basic;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.network.game.in.basic.AskDate;
import fr.quatrevieux.araknemu.network.game.out.basic.ServerDate;
import fr.quatrevieux.araknemu.network.game.out.basic.ServerTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SendDateAndTimeTest extends GameBaseCase {
    private SendDateAndTime handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new SendDateAndTime();
    }

    @Test
    void handler() throws Exception {
        handler.handle(session, new AskDate());

        requestStack.assertContains(ServerDate.class);
        requestStack.assertContains(ServerTime.class);
    }
}
