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

package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.player.characteristic.BaseCharacteristics;
import fr.quatrevieux.araknemu.game.player.characteristic.PlayerCharacteristics;
import fr.quatrevieux.araknemu.game.player.characteristic.SpecialEffects;
import fr.quatrevieux.araknemu.game.player.characteristic.event.CharacteristicsChanged;
import fr.quatrevieux.araknemu.game.player.inventory.slot.BeltSlot;
import fr.quatrevieux.araknemu.game.player.inventory.slot.BootsSlot;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlayerCharacteristicsTest extends GameBaseCase {
    private PlayerCharacteristics characteristics;
    private MutableCharacteristics base;
    private ListenerAggregate dispatcher;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        base = MutableCharacteristics.class.cast(gamePlayer(true).properties().characteristics().base());

        characteristics = new PlayerCharacteristics(
            dispatcher = new DefaultListenerAggregate(),
            gamePlayer(),
            gamePlayer().entity()
        );
    }

    @Test
    void defaults() {
        assertInstanceOf(BaseCharacteristics.class, characteristics.base());
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
        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        gamePlayer().inventory().add(container.get(ItemService.class).create(2425, true), 1, 0);
        gamePlayer().inventory().add(container.get(ItemService.class).create(2411, true), 1, 6);

        AtomicReference<CharacteristicsChanged> ref = new AtomicReference<>();
        dispatcher.add(CharacteristicsChanged.class, ref::set);

        characteristics.rebuildStuffStats();

        assertNotNull(ref.get());
        assertEquals(55, characteristics.stuff().get(Characteristic.INTELLIGENCE));
        assertEquals(55, characteristics.stuff().get(Characteristic.STRENGTH));
    }

    @Test
    void rebuildStuffStatsWithFullItemSet() throws SQLException, ContainerException, InventoryException {
        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        gamePlayer().inventory().add(container.get(ItemService.class).create(2411, true), 1, 6);
        gamePlayer().inventory().add(container.get(ItemService.class).create(2414, true), 1, 7);
        gamePlayer().inventory().add(container.get(ItemService.class).create(2416, true), 1, 1);
        gamePlayer().inventory().add(container.get(ItemService.class).create(2419, true), 1, 2);
        gamePlayer().inventory().add(container.get(ItemService.class).create(2422, true), 1, 5);
        gamePlayer().inventory().add(container.get(ItemService.class).create(2425, true), 1, 0);
        gamePlayer().inventory().add(container.get(ItemService.class).create(2428, true), 1, 3);

        characteristics.rebuildStuffStats();

        assertEquals(100, gamePlayer().properties().characteristics().stuff().get(Characteristic.STRENGTH));
        assertEquals(100, gamePlayer().properties().characteristics().stuff().get(Characteristic.INTELLIGENCE));
        assertEquals(20, gamePlayer().properties().characteristics().stuff().get(Characteristic.WISDOM));
        assertEquals(139, gamePlayer().properties().characteristics().stuff().get(Characteristic.VITALITY));
        assertEquals(5, gamePlayer().properties().characteristics().stuff().get(Characteristic.FIXED_DAMAGE));
        assertEquals(15, gamePlayer().properties().characteristics().stuff().get(Characteristic.PERCENT_DAMAGE));
        assertEquals(1, gamePlayer().properties().characteristics().stuff().get(Characteristic.ACTION_POINT));
        assertEquals(1, gamePlayer().properties().characteristics().stuff().get(Characteristic.MAX_SUMMONED_CREATURES));
    }

    @Test
    void rebuildSpecialEffectsWithItemSetWithSpecialEffects() throws ContainerException, SQLException, InventoryException {
        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        gamePlayer().inventory().add(container.get(ItemService.class).create(8213), 1, 0);
        gamePlayer().inventory().add(container.get(ItemService.class).create(8219), 1, 2);
        gamePlayer().inventory().add(container.get(ItemService.class).create(8225), 1, BootsSlot.SLOT_ID);
        gamePlayer().inventory().add(container.get(ItemService.class).create(8231), 1, 7);
        gamePlayer().inventory().add(container.get(ItemService.class).create(8237), 1, BeltSlot.SLOT_ID);
        gamePlayer().inventory().add(container.get(ItemService.class).create(8243), 1, 6);

        gamePlayer().properties().characteristics().specials().clear();
        characteristics.rebuildSpecialEffects();

        assertEquals(60, gamePlayer().properties().characteristics().specials().get(SpecialEffects.Type.INITIATIVE));
    }

    @Test
    void getFromBaseAndStuff() throws SQLException, ContainerException, InventoryException {
        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        gamePlayer().inventory().add(container.get(ItemService.class).create(2425, true), 1, 0);
        gamePlayer().inventory().add(container.get(ItemService.class).create(2411, true), 1, 6);

        base.set(Characteristic.INTELLIGENCE, 250);
        characteristics.rebuildStuffStats();

        assertEquals(305, characteristics.get(Characteristic.INTELLIGENCE));
        assertEquals(105, characteristics.get(Characteristic.STRENGTH));
    }

    @Test
    void boostCharacteristicSuccess() throws SQLException, ContainerException {
        gamePlayer().entity().setBoostPoints(10);

        AtomicReference<CharacteristicsChanged> ref = new AtomicReference<>();
        dispatcher.add(CharacteristicsChanged.class, ref::set);

        characteristics.boostCharacteristic(Characteristic.STRENGTH);

        assertNotNull(ref.get());
        assertEquals(51, characteristics.base().get(Characteristic.STRENGTH));
        assertEquals(7, gamePlayer().entity().boostPoints());
    }

    @Test
    void boostCharacteristicNotEnoughPoints() throws SQLException, ContainerException {
        gamePlayer().entity().setBoostPoints(1);

        AtomicReference<CharacteristicsChanged> ref = new AtomicReference<>();
        dispatcher.add(CharacteristicsChanged.class, ref::set);

        assertThrows(IllegalStateException.class, () -> characteristics.boostCharacteristic(Characteristic.STRENGTH));

        assertNull(ref.get());
        assertEquals(50, characteristics.base().get(Characteristic.STRENGTH));
        assertEquals(1, gamePlayer().entity().boostPoints());
    }

    @Test
    void boostCharacteristicBadStats() throws SQLException, ContainerException {
        assertThrows(NoSuchElementException.class, () -> characteristics.boostCharacteristic(Characteristic.ACTION_POINT));
    }

    @Test
    void initiative() {
        characteristics.specials().add(SpecialEffects.Type.INITIATIVE, 200);

        assertEquals(473, characteristics.initiative());
    }

    @Test
    void discernment() throws SQLException, ContainerException {
        characteristics.specials().add(SpecialEffects.Type.DISCERNMENT, 15);
        gamePlayer().entity().stats().set(Characteristic.LUCK, 120);

        assertEquals(127, characteristics.discernment());
    }

    @Test
    void rebuildSpecialEffects() throws SQLException, ContainerException, InventoryException {
        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        gamePlayer().entity().stats().set(Characteristic.VITALITY, 50);
        gamePlayer().inventory().add(container.get(ItemService.class).create(2414, true), 1, 7);
        gamePlayer().inventory().add(container.get(ItemService.class).create(2428, true), 1, 3);
        gamePlayer().properties().characteristics().rebuildSpecialEffects();

        assertEquals(500, gamePlayer().properties().characteristics().specials().get(SpecialEffects.Type.PODS));
        assertEquals(300, gamePlayer().properties().characteristics().specials().get(SpecialEffects.Type.INITIATIVE));
    }

    @Test
    void pods() {
        assertEquals(1250, characteristics.pods());

        characteristics.specials().add(SpecialEffects.Type.PODS, 100);
        assertEquals(1350, characteristics.pods());

        characteristics.base().add(Characteristic.STRENGTH, 100);
        assertEquals(1850, characteristics.pods());
    }
}
