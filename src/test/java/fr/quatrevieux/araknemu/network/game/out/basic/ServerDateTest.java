package fr.quatrevieux.araknemu.network.game.out.basic;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

class ServerDateTest {
    @Test
    void generate() {
        Calendar calendar = new GregorianCalendar(648, 5, 12, 15, 25, 32);

        assertEquals("BD0648|06|12", new ServerDate(calendar).toString());
    }
}
