package fr.quatrevieux.araknemu.network.realm.out;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SelectServerErrorTest {
    @Test
    void packet() {
        assertEquals("AXEr", new SelectServerError(SelectServerError.Error.CANT_SELECT).toString());
    }
}