package fr.quatrevieux.araknemu._test;

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
        assertTrue(value <= max);
        assertTrue(value >= min);
    }
}
