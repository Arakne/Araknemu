package fr.quatrevieux.araknemu.network.realm.out;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PseudoTest {
    @Test
    void test_toString() {
        assertEquals("Adpseudo", new Pseudo("pseudo").toString());
    }

}