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

package fr.quatrevieux.araknemu.game.handler.account;

import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.core.network.exception.CloseWithPacket;
import fr.quatrevieux.araknemu.network.game.in.account.ChoosePlayingCharacter;
import fr.quatrevieux.araknemu.network.game.out.chat.ChannelSubscribed;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SelectCharacterTest extends GameBaseCase {
    private SelectCharacter handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new SelectCharacter(
            container.get(PlayerService.class)
        );

        login();

        dataSet
            .pushRaces()
            .pushSpells()
            .use(Player.class)
            .use(PlayerItem.class)
            .use(PlayerSpell.class)
        ;
    }

    @Test
    void handleBadPlayer() throws Exception {
        try {
            handler.handle(session, new ChoosePlayingCharacter(123));
        } catch (CloseWithPacket e) {
            assertEquals("ASE", e.packet().toString());
        }
    }

    @Test
    void handleSuccess() throws Exception {
        int id = dataSet.push(new Player(-1, 1, 2, "Bob", Race.FECA, Sex.MALE, new Colors(123, 456, 789), 23, null)).id();

        handler.handle(session, new ChoosePlayingCharacter(id));

        requestStack.assertOne("ASK|1|Bob|23||0|10|7b|1c8|315|");
        requestStack.assertLast(Error.welcome());
    }

    @Test
    void handleWillSendChatChannels() throws Exception {
        int id = dataSet.push(new Player(-1, 1, 2, "Bob", Race.FECA, Sex.MALE, new Colors(123, 456, 789), 23, null, new Position(10300, 123), EnumSet.of(ChannelType.INFO, ChannelType.PRIVATE), 0, 0, -1, 0, new Position(10540, 210), 0)).id();

        handler.handle(session, new ChoosePlayingCharacter(id));

        requestStack.assertOne(new ChannelSubscribed(EnumSet.of(ChannelType.INFO, ChannelType.PRIVATE)));
    }

    @Test
    void cannotReselectCharacter() throws Exception {
        int id = dataSet.push(new Player(-1, 1, 2, "Bob", Race.FECA, Sex.MALE, new Colors(123, 456, 789), 23, null)).id();

        handlePacket(new ChoosePlayingCharacter(id));
        assertThrows(CloseWithPacket.class, () -> handlePacket(new ChoosePlayingCharacter(id)));
    }
}
