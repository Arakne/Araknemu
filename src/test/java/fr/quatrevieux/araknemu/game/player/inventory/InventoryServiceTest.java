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

package fr.quatrevieux.araknemu.game.player.inventory;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.event.ExplorationPlayerCreated;
import fr.quatrevieux.araknemu.game.fight.fighter.event.PlayerFighterCreated;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.exception.ItemNotFoundException;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.SendFighterAccessories;
import fr.quatrevieux.araknemu.game.listener.map.SendAccessories;
import fr.quatrevieux.araknemu.game.listener.player.inventory.SaveInventoryChange;
import fr.quatrevieux.araknemu.game.listener.player.inventory.SendInventoryUpdate;
import fr.quatrevieux.araknemu.game.listener.player.inventory.SendWeight;
import fr.quatrevieux.araknemu.game.listener.player.inventory.UpdateStuffStats;
import fr.quatrevieux.araknemu.game.listener.player.inventory.itemset.ApplyItemSetSpecialEffects;
import fr.quatrevieux.araknemu.game.listener.player.inventory.itemset.InitializeItemSets;
import fr.quatrevieux.araknemu.game.listener.player.inventory.itemset.SendItemSetChange;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InventoryServiceTest extends GameBaseCase {
    private InventoryService service;
    private PlayerItemRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.use(PlayerItem.class);
        dataSet.pushItemTemplates();

        service = new InventoryService(
            repository = container.get(PlayerItemRepository.class),
            container.get(ItemService.class)
        );
    }

    @Test
    void loadEmptyInventory() throws SQLException {
        PlayerInventory inventory = service.load(new Player(5)).attach(gamePlayer());

        assertFalse(inventory.iterator().hasNext());
    }

    @Test
    void loadInventory() throws ItemNotFoundException, SQLException {
        Player player = new Player(5);

        repository.add(new PlayerItem(5, 3, 39, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "")), 1, 0));
        repository.add(new PlayerItem(5, 8, 40, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")), 1, 1));
        repository.add(new PlayerItem(5, 32, 284, new ArrayList<>(), 10, -1));

        PlayerInventory inventory = service.load(player).attach(gamePlayer());

        assertEquals(39, inventory.get(3).templateId());
        assertEquals(40, inventory.get(8).templateId());
        assertEquals(284, inventory.get(32).templateId());
    }

    @Test
    void playerLoadedListener() throws SQLException, ContainerException {
        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        dispatcher.dispatch(new PlayerLoaded(gamePlayer()));

        assertDispatcherContainsListeners(
            gamePlayer().dispatcher(),
            UpdateStuffStats.class,
            SendItemSetChange.class,
            InitializeItemSets.class,
            ApplyItemSetSpecialEffects.class
        );

        assertSubscriberRegistered(new SendInventoryUpdate(gamePlayer()), gamePlayer().dispatcher());
        assertSubscriberRegistered(new SaveInventoryChange(service), gamePlayer().dispatcher());
        assertSubscriberRegistered(new SendWeight(gamePlayer()), gamePlayer().dispatcher());
    }

    @Test
    void explorationPlayerCreatedListener() throws Exception {
        ExplorationPlayer player = new ExplorationPlayer(gamePlayer());

        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);
        dispatcher.dispatch(new ExplorationPlayerCreated(player));

        assertTrue(player.dispatcher().has(SendAccessories.class));
    }

    @Test
    void playerFighterCreatedListener() throws Exception {
        PlayerFighter fighter = new PlayerFighter(gamePlayer());

        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);
        dispatcher.dispatch(new PlayerFighterCreated(fighter));

        assertTrue(fighter.dispatcher().has(SendFighterAccessories.class));
    }

    @Test
    void deleteItemEntry() {
        InventoryEntry entry = new InventoryEntry(
            null,
            new PlayerItem(1, 1, 284, new ArrayList<>(), 5, -1),
            null
        );

        container.get(PlayerItemRepository.class).add(entry.entity());

        service.deleteItemEntry(entry);

        assertFalse(container.get(PlayerItemRepository.class).has(entry.entity()));
    }

    @Test
    void updateItemEntry() throws ContainerException {
        InventoryEntry entry = new InventoryEntry(
            null,
            new PlayerItem(1, 1, 284, new ArrayList<>(), 5, -1),
            null
        );

        container.get(PlayerItemRepository.class).add(entry.entity());
        entry.entity().setPosition(1);

        service.updateItemEntry(entry);

        assertEquals(1, container.get(PlayerItemRepository.class).get(entry.entity()).position());
    }

    @Test
    void saveItemEntry() throws ContainerException {
        InventoryEntry entry = new InventoryEntry(
            null,
            new PlayerItem(1, 1, 284, new ArrayList<>(), 5, -1),
            null
        );

        service.saveItemEntry(entry);

        assertTrue(container.get(PlayerItemRepository.class).has(entry.entity()));
    }
}
