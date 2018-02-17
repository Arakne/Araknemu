package fr.quatrevieux.araknemu.network.game.in.account;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AskBoostTest {
    @Test
    void parse() {
        assertEquals(Characteristic.STRENGTH, new AskBoost.Parser().parse("10").characteristic());
    }
}
