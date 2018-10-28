package fr.quatrevieux.araknemu.network.game.out.info;

import fr.quatrevieux.araknemu.data.value.Interval;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorTest {
    @Test
    void welcome() {
        assertEquals("Im189;", Error.welcome().toString());
    }

    @Test
    void cantDoOnServer() {
        assertEquals("Im1226;", Error.cantDoOnServer().toString());
    }

    @Test
    void cantLearnSpell() {
        assertEquals("Im17;123", Error.cantLearnSpell(123).toString());
    }

    @Test
    void cantCastNotFound() {
        assertEquals("Im1169;", Error.cantCastNotFound().toString());
    }

    @Test
    void cantCastInvalidCell() {
        assertEquals("Im1193;", Error.cantCastInvalidCell().toString());
    }

    @Test
    void cantCastCellNotAvailable() {
        assertEquals("Im1172;", Error.cantCastCellNotAvailable().toString());
    }

    @Test
    void cantCastLineLaunch() {
        assertEquals("Im1173;", Error.cantCastLineLaunch().toString());
    }

    @Test
    void cantCastNotEnoughActionPoints() {
        assertEquals("Im1170;4~5", Error.cantCastNotEnoughActionPoints(4, 5).toString());
    }

    @Test
    void cantCastBadState() {
        assertEquals("Im1116;", Error.cantCastBadState().toString());
    }

    @Test
    void cantCastBadRange() {
        assertEquals("Im1171;2~5~1", Error.cantCastBadRange(new Interval(2, 5), 1).toString());
    }

    @Test
    void cantCast() {
        assertEquals("Im1175;", Error.cantCast().toString());
    }
}
