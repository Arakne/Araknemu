package fr.quatrevieux.araknemu.game.handler.account;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.network.game.in.account.AskRegionalVersion;
import fr.quatrevieux.araknemu.network.game.out.account.RegionalVersion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SendRegionalVersionTest extends GameBaseCase {
    private SendRegionalVersion handler;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        handler = new SendRegionalVersion();
    }

    @Test
    void handle() throws Exception {
        handler.handle(session, new AskRegionalVersion());

        requestStack.assertLast(new RegionalVersion(0));
    }
}
