package fr.quatrevieux.araknemu.game.exploration.npc;

import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NpcSpriteTest extends GameBaseCase {
    @Test
    void simpleNpc() {
        NpcSprite sprite = new NpcSprite(new GameNpc(
            new Npc(472, 878, new Position(10340, 82), Direction.SOUTH_EAST, new int[] {3786}),
            new NpcTemplate(878, 40, 100, 100, Sex.MALE, new Colors(8158389, 13677665, 3683117), "0,20f9,2a5,1d5e,1b9e", -1, 0),
            new ArrayList<>()
        ));

        assertEquals("82;1;0;-47204;878;-4;40^100x100;0;7c7cb5;d0b461;38332d;0,20f9,2a5,1d5e,1b9e;;0", sprite.toString());
    }

    @Test
    void withCustomArtworkAndExtraClip() {
        NpcSprite sprite = new NpcSprite(new GameNpc(
            new Npc(472, 878, new Position(10340, 82), Direction.SOUTH_EAST, new int[] {3786}),
            new NpcTemplate(878, 40, 100, 100, Sex.MALE, new Colors(8158389, 13677665, 3683117), "0,20f9,2a5,1d5e,1b9e", 4, 9092),
            new ArrayList<>()
        ));

        assertEquals("82;1;0;-47204;878;-4;40^100x100;0;7c7cb5;d0b461;38332d;0,20f9,2a5,1d5e,1b9e;4;9092", sprite.toString());
    }
}
