package fr.quatrevieux.araknemu.network.game.out.area;

import fr.quatrevieux.araknemu.data.constant.Alignment;
import fr.quatrevieux.araknemu.data.living.entity.environment.SubArea;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SubAreaListTest {
    @Test
    void generate() {
        assertEquals(
            "al|1;1|3;0",
            new SubAreaList(Arrays.asList(
                new SubArea(1, 2, "", true, Alignment.BONTARIAN),
                new SubArea(2, 2, "", true, Alignment.NONE),
                new SubArea(3, 2, "", true, Alignment.NEUTRAL)
            )).toString()
        );
    }
}
