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

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.game.chat.event.ChannelSubscriptionAdded;
import fr.quatrevieux.araknemu.game.chat.event.ChannelSubscriptionRemoved;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Set of subscribed channels with a dispatcher for synchronise operations
 */
public final class ChannelSet implements Set<ChannelType> {
    private final Set<ChannelType> set;
    private final Dispatcher dispatcher;

    public ChannelSet(Set<ChannelType> set, Dispatcher dispatcher) {
        this.set = set;
        this.dispatcher = dispatcher;
    }

    @Override
    public @NonNegative int size() {
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
    @SuppressWarnings({"toarray.nullable.elements.not.newarray", "override.param", "return"})
    public <T> T @NonNull[] toArray(T @NonNull[] ts) {
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
    public boolean equals(@Nullable Object obj) {
        return set.equals(obj);
    }
}
