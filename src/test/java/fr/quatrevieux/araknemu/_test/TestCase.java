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

package fr.quatrevieux.araknemu._test;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.core.network.exception.WritePacket;
import fr.quatrevieux.araknemu.game.listener.player.inventory.SendInventoryUpdate;
import org.junit.jupiter.api.function.Executable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class TestCase {
    public void assertInstanceOf(Class type, Object object) {
        assertNotNull(object, "The object should not be null");
        assertTrue(type.isInstance(object), "Invalid instance. Expects " + type.getName() + " but get " + object.getClass().getName());
    }

    public void assertContainsOnly(Class type, Object[] objects) {
        for (Object object : objects) {
            assertInstanceOf(type, object);
        }
    }

    public void assertContainsOnly(Class type, Collection objects) {
        assertContainsOnly(type, objects.toArray());
    }

    public void assertContainsType(Class type, Object[] objects) {
        for (Object object : objects) {
            if (type.isInstance(object)) {
                return;
            }
        }

        fail("Cannot found element of type " + type.getName());
    }

    public void assertContainsType(Class type, Collection objects) {
        assertContainsType(type, objects.toArray());
    }

    public void assertCount(int count, Object[] objects) {
        assertEquals(count, objects.length, "Invalid count");
    }

    public void assertCount(int count, Collection objects) {
        assertEquals(count, objects.size(), "Invalid count");
    }

    public void assertContains(Object expected, Collection collection) {
        assertTrue(collection.contains(expected), "The collection do not contains " + expected);
    }

    public void assertContainsAll(Collection collection, Object... objects) {
        assertTrue(collection.containsAll(Arrays.asList(objects)));
    }

    public void assertCollectionEquals(Collection current, Object... values) {
        assertCount(values.length, current);
        assertContainsAll(current, values);
    }

    public void assertBetween(int min, int max, int value) {
        assertTrue(value <= max, "Expected between " + min + " and " + max + " but get " + value);
        assertTrue(value >= min, "Expected between " + min + " and " + max + " but get " + value);
    }

    public void assertBetween(double min, double max, double value) {
        assertTrue(value <= max, "Expected between " + min + " and " + max + " but get " + value);
        assertTrue(value >= min, "Expected between " + min + " and " + max + " but get " + value);
    }

    public void assertBetween(long min, long max, long value) {
        assertTrue(value <= max, "Expected between " + min + " and " + max + " but get " + value);
        assertTrue(value >= min, "Expected between " + min + " and " + max + " but get " + value);
    }

    public void assertErrorPacket(Object excpectedPacket, Executable executable) {
        try {
            executable.execute();

            fail("Expects an ErrorPacket");
        } catch (Throwable e) {
            assertInstanceOf(WritePacket.class, e);
            assertEquals(excpectedPacket.toString(), WritePacket.class.cast(e).packet().toString(), "Invalid packet");
        }
    }

    public void assertRegex(String pattern, String value) {
        assertTrue(value.matches(pattern), value + " do not match with regex " + pattern);
    }

    public <T> T readField(Object object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);

        field.setAccessible(true);

        return (T) field.get(object);
    }

    public void assertDispatcherContainsListener(Class<? extends Listener> listenerClass, ListenerAggregate dispatcher) {
        assertTrue(dispatcher.has(listenerClass), "Failing expecting that dispatcher contains listener " + listenerClass.getSimpleName());
    }

    public void assertDispatcherContainsListeners(ListenerAggregate dispatcher, Class<? extends Listener>... listenerClasses) {
        for (Class<? extends Listener> listenerClass : listenerClasses) {
            assertTrue(dispatcher.has(listenerClass), "Failing expecting that dispatcher contains listener " + listenerClass.getSimpleName());
        }
    }

    public void assertSubscriberRegistered(EventsSubscriber subscriber, ListenerAggregate dispatcher) {
        for (Listener listener : subscriber.listeners()) {
            assertTrue(dispatcher.has(listener.getClass()), "Failing expecting that subscriber " + subscriber.getClass().getSimpleName() + " is registered into the dispatcher");
            return;
        }
    }
}
