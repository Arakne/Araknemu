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

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.account.event.CharacterCreationStarted;
import fr.quatrevieux.araknemu.game.chat.ChannelType;

import java.util.Set;

/**
 * Add chat channels on character creation
 */
public final class AddChatChannels implements Listener<CharacterCreationStarted> {
    private final GameConfiguration.ChatConfiguration configuration;
    private final Transformer<Set<ChannelType>> transformer;

    public AddChatChannels(GameConfiguration.ChatConfiguration configuration, Transformer<Set<ChannelType>> transformer) {
        this.configuration = configuration;
        this.transformer = transformer;
    }

    @Override
    public void on(CharacterCreationStarted event) {
        final Set<ChannelType> channels = transformer.unserialize(configuration.defaultChannels());

        if (event.character().account().isMaster()) {
            channels.addAll(
                transformer.unserialize(configuration.adminChannels())
            );
        }

        event.character().character().setChannels(channels);
    }

    @Override
    public Class<CharacterCreationStarted> event() {
        return CharacterCreationStarted.class;
    }
}
