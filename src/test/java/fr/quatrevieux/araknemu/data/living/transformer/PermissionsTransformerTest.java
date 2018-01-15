package fr.quatrevieux.araknemu.data.living.transformer;

import fr.quatrevieux.araknemu.common.account.Permission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class PermissionsTransformerTest {
    private PermissionsTransformer transformer;

    @BeforeEach
    public void setUp() throws Exception {
        transformer = new PermissionsTransformer();
    }

    @Test
    void serializeNull() {
        assertEquals(0, transformer.serialize(null));
    }

    @Test
    void serializeEmpty() {
        assertEquals(0, transformer.serialize(EnumSet.noneOf(Permission.class)));
    }

    @Test
    void serializeOne() {
        assertEquals(2, transformer.serialize(EnumSet.of(Permission.SUPER_ADMIN)));
    }

    @Test
    void serializeAll() {
        assertEquals(3, transformer.serialize(EnumSet.allOf(Permission.class)));
    }

    @Test
    void unserializeZero() {
        assertEquals(
            EnumSet.noneOf(Permission.class),
            transformer.unserialize(0)
        );
    }

    @Test
    void unserializeOne() {
        assertEquals(
            EnumSet.of(Permission.SUPER_ADMIN),
            transformer.unserialize(2)
        );
    }

    @Test
    void unserializeTwo() {
        assertEquals(
            EnumSet.of(Permission.ACCESS, Permission.SUPER_ADMIN),
            transformer.unserialize(3)
        );
    }

    @Test
    void unserializeUndefined() {
        assertEquals(
            EnumSet.noneOf(Permission.class),
            transformer.unserialize(4500)
        );
    }
}
