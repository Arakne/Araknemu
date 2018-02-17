package fr.quatrevieux.araknemu.network.game.out.object;

import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.account.CharacterAccessories;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SpriteAccessoriesTest {
    @Test
    void generate() {
        assertEquals(
            "Oa123|,c,17,,",
            new SpriteAccessories(
                123,
                new CharacterAccessories(
                    Arrays.asList(
                        new PlayerItem(1, 1, 12, null, 1, 6),
                        new PlayerItem(1, 1, 23, null, 1, 7)
                    )
                )
            ).toString()
        );
    }
}