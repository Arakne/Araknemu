package fr.quatrevieux.araknemu.network.game.out.basic;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

class ServerTimeTest {
    @Test
    void generate() {
        Calendar calendar = new GregorianCalendar(648, 5, 12, 15, 25, 32);

        assertEquals("BT-41703928468000", new ServerTime(calendar).toString());
    }
}