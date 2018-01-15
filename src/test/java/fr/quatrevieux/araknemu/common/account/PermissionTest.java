package fr.quatrevieux.araknemu.common.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PermissionTest {
    @Test
    void id() {
        assertEquals(1, Permission.ACCESS.id());
        assertEquals(2, Permission.SUPER_ADMIN.id());
    }

    @Test
    void match() {
        assertTrue(Permission.ACCESS.match(3));
        assertTrue(Permission.ACCESS.match(1));
        assertTrue(Permission.SUPER_ADMIN.match(3));
        assertFalse(Permission.SUPER_ADMIN.match(1));
    }
}
