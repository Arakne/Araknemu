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

package fr.quatrevieux.araknemu.game.listener.player.chat;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.transformer.ChannelsTransformer;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.account.AccountCharacter;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.account.event.CharacterCreationStarted;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddChatChannelsTest extends GameBaseCase {
    private AddChatChannels listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new AddChatChannels(
            container.get(GameConfiguration.class).chat(),
            new ChannelsTransformer()
        );
    }

    @Test
    void onSimpleCharacterCreation() throws ContainerException {
        AccountCharacter character = new AccountCharacter(
            new GameAccount(
                new Account(5, "simple", "", ""),
                container.get(AccountService.class),
                2
            ),
            new Player(2)
        );

        listener.on(
            new CharacterCreationStarted(character)
        );

        assertEquals(
            EnumSet.of(
                ChannelType.MESSAGES,
                ChannelType.FIGHT_TEAM,
                ChannelType.PRIVATE,
                ChannelType.GROUP,
                ChannelType.TRADE,
                ChannelType.RECRUITMENT,
                ChannelType.PVP,
                ChannelType.GUILD,
                ChannelType.INFO
            ),
            character.character().channels()
        );
    }

    @Test
    void onAdminCharacterCreation() throws ContainerException {
        AccountCharacter character = new AccountCharacter(
            new GameAccount(
                new Account(5, "admin", "", "admin", EnumSet.allOf(Permission.class), "", ""),
                container.get(AccountService.class),
                2
            ),
            new Player(2)
        );

        listener.on(
            new CharacterCreationStarted(character)
        );

        assertEquals(
            EnumSet.of(
                ChannelType.MESSAGES,
                ChannelType.FIGHT_TEAM,
                ChannelType.PRIVATE,
                ChannelType.GROUP,
                ChannelType.TRADE,
                ChannelType.RECRUITMENT,
                ChannelType.PVP,
                ChannelType.GUILD,
                ChannelType.INFO,
                ChannelType.ADMIN
            ),
            character.character().channels()
        );
    }
}
