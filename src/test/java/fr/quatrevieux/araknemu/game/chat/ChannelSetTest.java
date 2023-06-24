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

package fr.quatrevieux.araknemu.game.chat;

import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.chat.event.ChannelSubscriptionAdded;
import fr.quatrevieux.araknemu.game.chat.event.ChannelSubscriptionRemoved;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChannelSetTest {
    private Set<ChannelType> base;
    private ChannelSet channelSet;
    private ListenerAggregate dispatcher;

    @BeforeEach
    void setUp() {
        base = EnumSet.noneOf(ChannelType.class);
        channelSet = new ChannelSet(
            base,
            dispatcher = new DefaultListenerAggregate()
        );
    }

    @Test
    void size() {
        channelSet.add(ChannelType.PRIVATE);
        channelSet.add(ChannelType.MESSAGES);

        assertEquals(2, channelSet.size());
    }

    @Test
    void isEmpty() {
        assertTrue(channelSet.isEmpty());
        channelSet.add(ChannelType.PRIVATE);
        assertFalse(channelSet.isEmpty());
    }

    @Test
    void contains() {
        channelSet.add(ChannelType.PRIVATE);
        channelSet.add(ChannelType.MESSAGES);

        assertTrue(channelSet.contains(ChannelType.MESSAGES));
        assertFalse(channelSet.contains(ChannelType.ADMIN));
    }

    @Test
    void containsAll() {
        channelSet.add(ChannelType.PRIVATE);
        channelSet.add(ChannelType.MESSAGES);

        assertTrue(channelSet.containsAll(Arrays.asList(
            ChannelType.MESSAGES,
            ChannelType.PRIVATE
        )));
    }

    @Test
    void equals() {
        channelSet.add(ChannelType.PRIVATE);
        channelSet.add(ChannelType.MESSAGES);

        assertEquals(channelSet, base);
        assertNotEquals(channelSet, EnumSet.allOf(ChannelType.class));
    }

    @Test
    void addSuccess() {
        AtomicReference<Collection<ChannelType>> ref = new AtomicReference<>();
        dispatcher.add(ChannelSubscriptionAdded.class, evt -> ref.set(evt.channels()));

        assertTrue(channelSet.add(ChannelType.TRADE));
        assertTrue(channelSet.contains(ChannelType.TRADE));
        assertEquals(Collections.singleton(ChannelType.TRADE), ref.get());
    }

    @Test
    void addAlreadyExists() {
        channelSet.add(ChannelType.TRADE);

        AtomicReference<Collection<ChannelType>> ref = new AtomicReference<>();
        dispatcher.add(ChannelSubscriptionAdded.class, evt -> ref.set(evt.channels()));

        assertFalse(channelSet.add(ChannelType.TRADE));
        assertNull(ref.get());
    }

    @Test
    void removeSuccess() {
        channelSet.add(ChannelType.TRADE);

        AtomicReference<Collection<ChannelType>> ref = new AtomicReference<>();
        dispatcher.add(ChannelSubscriptionRemoved.class, evt -> ref.set(evt.channels()));

        assertTrue(channelSet.remove(ChannelType.TRADE));
        assertEquals(Collections.singleton(ChannelType.TRADE), ref.get());
        assertFalse(channelSet.contains(ChannelType.TRADE));
    }

    @Test
    void removeNotFound() {
        AtomicReference<Collection<ChannelType>> ref = new AtomicReference<>();
        dispatcher.add(ChannelSubscriptionRemoved.class, evt -> ref.set(evt.channels()));

        assertFalse(channelSet.remove(ChannelType.TRADE));
        assertNull(ref.get());
    }

    @Test
    void addAll() {
        channelSet.add(ChannelType.TRADE);

        AtomicReference<Collection<ChannelType>> ref = new AtomicReference<>();
        dispatcher.add(ChannelSubscriptionAdded.class, evt -> ref.set(evt.channels()));

        assertTrue(channelSet.addAll(EnumSet.of(ChannelType.INFO, ChannelType.MESSAGES)));
        assertTrue(channelSet.containsAll(EnumSet.of(ChannelType.INFO, ChannelType.MESSAGES)));
        assertEquals(EnumSet.of(ChannelType.INFO, ChannelType.MESSAGES), ref.get());
    }

    @Test
    void removeAll() {
        channelSet.add(ChannelType.TRADE);
        channelSet.add(ChannelType.INFO);
        channelSet.add(ChannelType.MESSAGES);

        AtomicReference<Collection<ChannelType>> ref = new AtomicReference<>();
        dispatcher.add(ChannelSubscriptionRemoved.class, evt -> ref.set(evt.channels()));

        assertTrue(channelSet.removeAll(Arrays.asList(ChannelType.INFO, ChannelType.MESSAGES)));
        assertEquals(Arrays.asList(ChannelType.INFO, ChannelType.MESSAGES), ref.get());
        assertFalse(channelSet.contains(ChannelType.INFO));
        assertFalse(channelSet.contains(ChannelType.MESSAGES));
    }
}
