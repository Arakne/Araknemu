package fr.quatrevieux.araknemu._test;

import static org.junit.jupiter.api.Assertions.*;

public class TestCase {
    public void assertInstanceOf(Class type, Object object) {
        assertNotNull(object, "The object should not be null");
        assertTrue(type.isInstance(object), "Invalid instance. Expects " + type.getName() + " but get " + object.getClass().getName());
    }

    public void assertConstainsOnly(Class type, Object[] objects) {
        for (Object object : objects) {
            assertInstanceOf(type, object);
        }
    }

    public void assertContainsType(Class type, Object[] objects) {
        for (Object object : objects) {
            if (type.isInstance(object)) {
                return;
            }
        }

        fail("Cannot found element of type " + type.getName());
    }

    public void assertCount(int count, Object[] objects) {
        assertEquals(count, objects.length, "Invalid count");
    }
}
