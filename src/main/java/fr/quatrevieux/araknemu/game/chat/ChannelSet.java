package fr.quatrevieux.araknemu.game.chat;

import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.common.ChannelSubscriptionAdded;
import fr.quatrevieux.araknemu.game.event.common.ChannelSubscriptionRemoved;

import java.util.*;

/**
 * Set of subscribed channels with a dispatcher for synchronise operations
 */
final public class ChannelSet implements Set<ChannelType> {
    final private Set<ChannelType> set;
    final private Dispatcher dispatcher;

    public ChannelSet(Set<ChannelType> set, Dispatcher dispatcher) {
        this.set = set;
        this.dispatcher = dispatcher;
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return set.contains(o);
    }

    @Override
    public Iterator<ChannelType> iterator() {
        return set.iterator();
    }

    @Override
    public Object[] toArray() {
        return set.toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return set.toArray(ts);
    }

    @Override
    public boolean add(ChannelType channelType) {
        if (set.add(channelType)) {
            dispatcher.dispatch(new ChannelSubscriptionAdded(Collections.singleton(channelType)));
            return true;
        }

        return false;
    }

    @Override
    public boolean remove(Object o) {
        if (set.remove(o)) {
            dispatcher.dispatch(new ChannelSubscriptionRemoved(Collections.singleton((ChannelType) o)));
            return true;
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return set.containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends ChannelType> collection) {
        if (set.addAll(collection)) {
            dispatcher.dispatch(new ChannelSubscriptionAdded((Collection<ChannelType>) collection));
            return true;
        }

        return false;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        if (set.removeAll(collection)) {
            dispatcher.dispatch(new ChannelSubscriptionRemoved((Collection<ChannelType>) collection));
            return true;
        }

        return false;
    }

    @Override
    public void clear() {
        set.clear();
        dispatcher.dispatch(new ChannelSubscriptionRemoved(EnumSet.allOf(ChannelType.class)));
    }

    @Override
    public boolean equals(Object obj) {
        return set.equals(obj);
    }
}
