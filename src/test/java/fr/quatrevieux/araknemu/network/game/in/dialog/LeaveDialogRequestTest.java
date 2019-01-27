package fr.quatrevieux.araknemu.network.game.in.dialog;

import fr.quatrevieux.araknemu._test.TestCase;
import org.junit.jupiter.api.Test;

class LeaveDialogRequestTest extends TestCase {
    @Test
    void parse() {
        assertInstanceOf(LeaveDialogRequest.class, new LeaveDialogRequest.Parser().parse(""));
    }
}