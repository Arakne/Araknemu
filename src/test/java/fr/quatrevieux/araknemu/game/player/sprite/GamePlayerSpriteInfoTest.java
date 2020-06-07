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

package fr.quatrevieux.araknemu.game.player.sprite;

import fr.arakne.utils.value.Colors;
import fr.arakne.utils.value.constant.Gender;
import fr.arakne.utils.value.constant.Race;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class GamePlayerSpriteInfoTest extends GameBaseCase {
    @Test
    void values() throws SQLException, ContainerException {
        gamePlayer(true);

        GamePlayerSpriteInfo spriteInfo = new GamePlayerSpriteInfo(
            dataSet.refresh(new Player(gamePlayer().id())),
            gamePlayer().inventory()
        );

        assertEquals(gamePlayer().id(), spriteInfo.id());
        assertEquals(10, spriteInfo.gfxId());
        assertEquals("Bob", spriteInfo.name());
        assertArrayEquals(new Colors(123, 456, 789).toArray(), spriteInfo.colors().toArray());
        assertEquals("100x100", spriteInfo.size().toString());
        assertEquals(Gender.MALE, spriteInfo.gender());
        assertEquals(Race.FECA, spriteInfo.race());
        assertEquals(gamePlayer().inventory().accessories(), spriteInfo.accessories());
    }
}