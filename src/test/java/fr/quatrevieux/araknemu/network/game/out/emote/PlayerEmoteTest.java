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
 * Copyright (c) 2017-2026 Leor Finacre
 */
package fr.quatrevieux.araknemu.network.game.out.emote;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Emote;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerEmoteTest extends GameBaseCase {
    @Test
    void generate() throws SQLException, ContainerException {
        ExplorationPlayer exploration = explorationPlayer();

        assertEquals("eUK1|1", new PlayerEmote(exploration, Emote.SIT, true).toString());
    }

    @Test
    void emoteId_should_return_constructor_value() throws SQLException, ContainerException {
        ExplorationPlayer exploration = explorationPlayer();
        PlayerEmote playerEmote = new PlayerEmote(exploration, Emote.FEAR, true);

        assertEquals(5, playerEmote.getEmote());
    }

    @Test
    void toString_activated_should_return_emote_id() throws SQLException, ContainerException {
        ExplorationPlayer exploration = explorationPlayer();
        PlayerEmote playerEmote = new PlayerEmote(exploration, Emote.SIT, true);

        assertEquals("eUK" + exploration.id() + "|1", playerEmote.toString());
    }

    @Test
    void toString_deactivated_should_return_zero() throws SQLException, ContainerException {
        ExplorationPlayer exploration = explorationPlayer();
        PlayerEmote playerEmote = new PlayerEmote(exploration, Emote.SIT, false);

        assertEquals("eUK" + exploration.id() + "|0", playerEmote.toString());
    }
}
