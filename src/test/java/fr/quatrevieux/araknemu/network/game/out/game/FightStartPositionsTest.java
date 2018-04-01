package fr.quatrevieux.araknemu.network.game.out.game;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FightStartPositionsTest {
    @Test
    void generate() {
        assertEquals(
            "GPa3btbYb_cacQcRc3c5dg|aWa_blbAbCbQb5b6cjcw|0",
            new FightStartPositions(
                new List[] {
                    Arrays.asList(55, 83, 114, 127, 128, 170, 171, 183, 185, 198),
                    Arrays.asList(48, 63, 75, 90, 92, 106, 121, 122, 137, 150)
                },
                0
            ).toString()
        );
    }
}
