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

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository;
import fr.quatrevieux.araknemu.game.exploration.event.ExplorationPlayerCreated;
import fr.quatrevieux.araknemu.game.fight.fighter.event.PlayerFighterCreated;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.SendFighterAccessories;
import fr.quatrevieux.araknemu.game.listener.map.SendAccessories;
import fr.quatrevieux.araknemu.game.listener.player.inventory.SaveInventoryChange;
import fr.quatrevieux.araknemu.game.listener.player.inventory.SendInventoryUpdate;
import fr.quatrevieux.araknemu.game.listener.player.inventory.SendWeight;
import fr.quatrevieux.araknemu.game.listener.player.inventory.UpdateStuffStats;
import fr.quatrevieux.araknemu.game.listener.player.inventory.itemset.ApplyItemSetSpecialEffects;
import fr.quatrevieux.araknemu.game.listener.player.inventory.itemset.InitializeItemSets;
import fr.quatrevieux.araknemu.game.listener.player.inventory.itemset.SendItemSetChange;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;

import java.util.stream.Collectors;

/**
 * Service for handle player inventory
 */
public final class InventoryService implements EventsSubscriber {
    private final PlayerItemRepository repository;
    private final ItemService service;

    public InventoryService(PlayerItemRepository repository, ItemService service) {
        this.repository = repository;
        this.service = service;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<PlayerLoaded>() {
                @Override
                public void on(PlayerLoaded event) {
                    registerInventoryListeners(event.player());
                }

                @Override
                public Class<PlayerLoaded> event() {
                    return PlayerLoaded.class;
                }
            },

            new Listener<ExplorationPlayerCreated>() {
                @Override
                public void on(ExplorationPlayerCreated event) {
                    event.player().dispatcher().add(new SendAccessories(event.player()));
                }

                @Override
                public Class<ExplorationPlayerCreated> event() {
                    return ExplorationPlayerCreated.class;
                }
            },

            new Listener<PlayerFighterCreated>() {
                @Override
                public void on(PlayerFighterCreated event) {
                    event.fighter().dispatcher().add(new SendFighterAccessories(event.fighter()));
                }

                @Override
                public Class<PlayerFighterCreated> event() {
                    return PlayerFighterCreated.class;
                }
            },
        };
    }

    /**
     * Load the inventory for the player
     *
     * @param player Player to load
     */
    public LoadedInventory load(Player player) {
        return new LoadedInventory(
            player,
            repository.byPlayer(player)
                .stream()
                .map(
                    entity -> new LoadedItem(
                        entity,
                        service.retrieve(entity.itemTemplateId(), entity.effects())
                    )
                )
                .collect(Collectors.toList())
        );
    }

    /**
     * Insert the item into database
     */
    public void saveItemEntry(InventoryEntry item) {
        repository.add(item.entity());
    }

    /**
     * Update the item data into database
     */
    public void updateItemEntry(InventoryEntry item) {
        repository.update(item.entity());
    }

    /**
     * Delete the item from database
     */
    public void deleteItemEntry(InventoryEntry item) {
        repository.delete(item.entity());
    }

    /**
     * Register inventory listeners for a player
     */
    private void registerInventoryListeners(GamePlayer player) {
        final ListenerAggregate dispatcher = player.dispatcher();

        dispatcher.register(new SendInventoryUpdate(player));
        dispatcher.register(new SaveInventoryChange(this));

        dispatcher.add(new InitializeItemSets(player));
        dispatcher.add(new SendItemSetChange(player));
        dispatcher.add(new ApplyItemSetSpecialEffects(player));

        // Must be the last registered listener : the stats will be sent
        dispatcher.add(new UpdateStuffStats(player));

        // Compute and send weight after stats updated
        dispatcher.register(new SendWeight(player));
    }

    static class LoadedItem {
        private final PlayerItem entity;
        private final Item item;

        public LoadedItem(PlayerItem entity, Item item) {
            this.entity = entity;
            this.item = item;
        }

        public PlayerItem entity() {
            return entity;
        }

        public Item item() {
            return item;
        }
    }
}
