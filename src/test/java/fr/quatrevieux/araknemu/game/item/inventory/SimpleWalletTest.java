package fr.quatrevieux.araknemu.game.item.inventory;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.living.entity.account.AccountBank;
import fr.quatrevieux.araknemu.game.item.inventory.event.KamasChanged;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class SimpleWalletTest extends TestCase {
    private AccountBank entity;
    private SimpleWallet wallet;
    private ListenerAggregate dispatcher;

    @BeforeEach
    void setUp() {
        entity = new AccountBank(0, 0, 0);
        dispatcher = new DefaultListenerAggregate();
        wallet = new SimpleWallet(entity, dispatcher);
    }

    @Test
    void kamas() {
        entity.setKamas(1450);
        assertEquals(1450, wallet.kamas());
    }

    @Test
    void addKamas() {
        entity.setKamas(1000);

        AtomicReference<KamasChanged> ref = new AtomicReference<>();
        dispatcher.add(KamasChanged.class, ref::set);

        wallet.addKamas(250);

        assertEquals(1250, wallet.kamas());
        assertEquals(1000, ref.get().lastQuantity());
        assertEquals(1250, ref.get().newQuantity());
    }

    @Test
    void addKamasWithNegativeAmountShouldRaiseException() {
        entity.setKamas(1000);

        AtomicReference<KamasChanged> ref = new AtomicReference<>();
        dispatcher.add(KamasChanged.class, ref::set);

        assertThrows(IllegalArgumentException.class, () -> wallet.addKamas(-250));

        assertEquals(1000, wallet.kamas());
        assertNull(ref.get());
    }

    @Test
    void removeKamas() {
        entity.setKamas(1000);

        AtomicReference<KamasChanged> ref = new AtomicReference<>();
        dispatcher.add(KamasChanged.class, ref::set);

        wallet.removeKamas(250);

        assertEquals(750, wallet.kamas());
        assertEquals(1000, ref.get().lastQuantity());
        assertEquals(750, ref.get().newQuantity());
    }

    @Test
    void removeKamasWithNegativeAmountShouldRaiseException() {
        entity.setKamas(1000);

        AtomicReference<KamasChanged> ref = new AtomicReference<>();
        dispatcher.add(KamasChanged.class, ref::set);

        assertThrows(IllegalArgumentException.class, () -> wallet.removeKamas(-250));

        assertEquals(1000, wallet.kamas());
        assertNull(ref.get());
    }

    @Test
    void removeKamasWithTooHighAmountShouldRaiseException() {
        entity.setKamas(1000);

        AtomicReference<KamasChanged> ref = new AtomicReference<>();
        dispatcher.add(KamasChanged.class, ref::set);

        assertThrows(IllegalArgumentException.class, () -> wallet.removeKamas(3000));

        assertEquals(1000, wallet.kamas());
        assertNull(ref.get());
    }
}