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

package fr.quatrevieux.araknemu.game.handler.chat;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.network.game.in.chat.SubscribeChannels;
import fr.quatrevieux.araknemu.network.game.out.chat.ChannelSubscribed;
import fr.quatrevieux.araknemu.network.game.out.chat.ChannelUnsubscribed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;

class SaveSubscriptionTest extends GameBaseCase {
    private SaveSubscription handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        gamePlayer(true);

        handler = new SaveSubscription();
    }

    @Test
    void handleAddSubscription() throws Exception {
        gamePlayer().subscriptions().clear();

        handler.handle(session, new SubscribeChannels(true, Arrays.asList(
            ChannelType.PRIVATE,
            ChannelType.MESSAGES
        )));

        gamePlayer().subscriptions().containsAll(
            Arrays.asList(
                ChannelType.PRIVATE,
                ChannelType.MESSAGES
            )
        );

        requestStack.assertLast(
            new ChannelSubscribed(
                Arrays.asList(
                    ChannelType.PRIVATE,
                    ChannelType.MESSAGES
                )
            )
        );
    }

    @Test
    void handleRemoveSubscriptions() throws Exception {
        handler.handle(session, new SubscribeChannels(false, Arrays.asList(
            ChannelType.PRIVATE,
            ChannelType.MESSAGES
        )));

        assertFalse(gamePlayer().subscriptions().contains(ChannelType.PRIVATE));
        assertFalse(gamePlayer().subscriptions().contains(ChannelType.MESSAGES));

        requestStack.assertLast(
            new ChannelUnsubscribed(
                Arrays.asList(
                    ChannelType.PRIVATE,
                    ChannelType.MESSAGES
                )
            )
        );
    }
}
