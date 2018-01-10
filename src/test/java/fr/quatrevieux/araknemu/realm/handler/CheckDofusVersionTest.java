package fr.quatrevieux.araknemu.realm.handler;

import fr.quatrevieux.araknemu.network.realm.in.DofusVersion;
import fr.quatrevieux.araknemu.network.realm.out.BadVersion;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckDofusVersionTest extends RealmBaseCase {
    private CheckDofusVersion handler;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new CheckDofusVersion(configuration);
    }

    @Test
    void testSuccess() {
        handler.handle(
            session,
            new DofusVersion("1.29.1")
        );

        assertTrue(channel.isAlive());
        requestStack.assertEmpty();
    }

    @Test
    void badVersion() {
        handler.handle(
            session,
            new DofusVersion("1.1.5")
        );

        assertClosed();
        requestStack.assertAll(new BadVersion("1.29.1"));
    }
}
