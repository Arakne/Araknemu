/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.exploration.npc;

import fr.arakne.utils.value.Colors;
import fr.arakne.utils.value.constant.Gender;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.arakne.utils.maps.constant.Direction;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NpcSpriteTest extends GameBaseCase {
    @Test
    void simpleNpc() {
        NpcSprite sprite = new NpcSprite(new GameNpc(
            new Npc(472, 878, new Position(10340, 82), Direction.SOUTH_EAST, new int[] {3786}),
            new NpcTemplate(878, 40, 100, 100, Gender.MALE, new Colors(8158389, 13677665, 3683117), "0,20f9,2a5,1d5e,1b9e", -1, 0, null),
            new ArrayList<>(),
            Collections.emptyList()
        ));

        assertEquals("82;1;0;-47204;878;-4;40^100x100;0;7c7cb5;d0b461;38332d;0,20f9,2a5,1d5e,1b9e;;0", sprite.toString());
    }

    @Test
    void withCustomArtworkAndExtraClip() {
        NpcSprite sprite = new NpcSprite(new GameNpc(
            new Npc(472, 878, new Position(10340, 82), Direction.SOUTH_EAST, new int[] {3786}),
            new NpcTemplate(878, 40, 100, 100, Gender.MALE, new Colors(8158389, 13677665, 3683117), "0,20f9,2a5,1d5e,1b9e", 4, 9092, null),
            new ArrayList<>(),
            Collections.emptyList()
        ));

        assertEquals("82;1;0;-47204;878;-4;40^100x100;0;7c7cb5;d0b461;38332d;0,20f9,2a5,1d5e,1b9e;4;9092", sprite.toString());
    }
}
