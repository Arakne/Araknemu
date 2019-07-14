package fr.quatrevieux.araknemu.network.game.out.exchange.movement.local;

import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class LocalExchangeObjectTest {
    @Test
    void generateAddObject() {
        ItemEntry entry = Mockito.mock(ItemEntry.class);
        Mockito.when(entry.id()).thenReturn(5);

        assertEquals("EMKO+5|3", new LocalExchangeObject(entry, 3).toString());
    }

    @Test
    void generateRemoveObject() {
        ItemEntry entry = Mockito.mock(ItemEntry.class);
        Mockito.when(entry.id()).thenReturn(5);

        assertEquals("EMKO-5", new LocalExchangeObject(entry, 0).toString());
    }
}
