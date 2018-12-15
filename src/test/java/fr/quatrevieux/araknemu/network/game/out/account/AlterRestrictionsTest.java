package fr.quatrevieux.araknemu.network.game.out.account;

import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.player.Restrictions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlterRestrictionsTest {
    @Test
    void string() {
        Restrictions restrictions = new Restrictions(new DefaultListenerAggregate());

        assertEquals("AR0", new AlterRestrictions(restrictions).toString());

        restrictions.set(Restrictions.Restriction.ALLOW_MOVE_ALL_DIRECTION);
        assertEquals("AR6bk", new AlterRestrictions(restrictions).toString());
    }
}
