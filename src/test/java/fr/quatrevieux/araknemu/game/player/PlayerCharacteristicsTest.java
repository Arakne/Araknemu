package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.common.CharacteristicsChanged;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class PlayerCharacteristicsTest extends GameBaseCase {
    private PlayerCharacteristics characteristics;
    private MutableCharacteristics base;
    private ListenerAggregate dispatcher;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        characteristics = new PlayerCharacteristics(
            base = new DefaultCharacteristics(),
            gamePlayer(true).inventory(),
            dispatcher = new DefaultListenerAggregate()
        );
    }

    @Test
    void defaults() {
        assertSame(base, characteristics.base());
        assertEquals(new DefaultCharacteristics(), characteristics.boost());
        assertEquals(new DefaultCharacteristics(), characteristics.feats());
        assertEquals(new DefaultCharacteristics(), characteristics.stuff());
    }

    @Test
    void getFromBaseStats() {
        base.set(Characteristic.INTELLIGENCE, 250);

        assertEquals(250, characteristics.get(Characteristic.INTELLIGENCE));
    }

    @Test
    void rebuildStuffStats() throws SQLException, ContainerException, InventoryException {
        dataSet.pushItemTemplates();

        gamePlayer().inventory().add(container.get(ItemService.class).create(2425, true), 1, 0);
        gamePlayer().inventory().add(container.get(ItemService.class).create(2411, true), 1, 6);

        AtomicReference<CharacteristicsChanged> ref = new AtomicReference<>();
        dispatcher.add(CharacteristicsChanged.class, ref::set);

        characteristics.rebuildStuffStats();

        assertNotNull(ref.get());
        assertEquals(50, characteristics.stuff().get(Characteristic.INTELLIGENCE));
        assertEquals(50, characteristics.stuff().get(Characteristic.STRENGTH));
    }

    @Test
    void getFromBaseAndStuff() throws SQLException, ContainerException, InventoryException {
        dataSet.pushItemTemplates();

        gamePlayer().inventory().add(container.get(ItemService.class).create(2425, true), 1, 0);
        gamePlayer().inventory().add(container.get(ItemService.class).create(2411, true), 1, 6);

        base.set(Characteristic.INTELLIGENCE, 250);
        characteristics.rebuildStuffStats();

        assertEquals(300, characteristics.get(Characteristic.INTELLIGENCE));
        assertEquals(50, characteristics.get(Characteristic.STRENGTH));
    }
}
