package fr.quatrevieux.araknemu.game.chat;

import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.chat.event.ChannelSubscriptionAdded;
import fr.quatrevieux.araknemu.game.chat.event.ChannelSubscriptionRemoved;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

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
