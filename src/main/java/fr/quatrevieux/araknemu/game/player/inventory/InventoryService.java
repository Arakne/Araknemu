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
import fr.quatrevieux.araknemu.game.listener.player.inventory.SaveDeletedItem;
import fr.quatrevieux.araknemu.game.listener.player.inventory.SaveItemPosition;
import fr.quatrevieux.araknemu.game.listener.player.inventory.SaveItemQuantity;
import fr.quatrevieux.araknemu.game.listener.player.inventory.SaveNewItem;
import fr.quatrevieux.araknemu.game.listener.player.inventory.SendItemData;
import fr.quatrevieux.araknemu.game.listener.player.inventory.SendItemDeleted;
import fr.quatrevieux.araknemu.game.listener.player.inventory.SendItemPosition;
import fr.quatrevieux.araknemu.game.listener.player.inventory.SendItemQuantity;
import fr.quatrevieux.araknemu.game.listener.player.inventory.SendKamas;
import fr.quatrevieux.araknemu.game.listener.player.inventory.SendWeight;
import fr.quatrevieux.araknemu.game.listener.player.inventory.UpdateStuffStats;
import fr.quatrevieux.araknemu.game.listener.player.inventory.itemset.ApplyItemSetSpecialEffects;
import fr.quatrevieux.araknemu.game.listener.player.inventory.itemset.InitializeItemSets;
import fr.quatrevieux.araknemu.game.listener.player.inventory.itemset.SendItemSetChange;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;

import java.util.stream.Collectors;

/**
 * Service for handle player inventory
 */
final public class InventoryService implements EventsSubscriber {
    static class LoadedItem {
        final private PlayerItem entity;
        final private Item item;

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

    final private PlayerItemRepository repository;
    final private ItemService service;

    public InventoryService(PlayerItemRepository repository, ItemService service) {
        this.repository = repository;
        this.service = service;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            // @todo refactor to external class
            new Listener<PlayerLoaded>() {
                @Override
                public void on(PlayerLoaded event) {
                    final ListenerAggregate dispatcher = event.player().dispatcher();

                    dispatcher.add(new SendKamas(event.player()));

                    dispatcher.add(new SendItemData(event.player()));
                    dispatcher.add(new SendItemPosition(event.player()));
                    dispatcher.add(new SendItemQuantity(event.player()));
                    dispatcher.add(new SendItemDeleted(event.player()));

                    dispatcher.add(new SaveNewItem(repository));
                    dispatcher.add(new SaveItemPosition(repository));
                    dispatcher.add(new SaveItemQuantity(repository));
                    dispatcher.add(new SaveDeletedItem(repository));

                    dispatcher.add(new InitializeItemSets(event.player()));
                    dispatcher.add(new SendItemSetChange(event.player()));
                    dispatcher.add(new ApplyItemSetSpecialEffects(event.player()));

                    // Must be the last registered listener : the stats will be sent
                    dispatcher.add(new UpdateStuffStats(event.player()));

                    // Compute and send weight after stats updated
                    dispatcher.register(new SendWeight(event.player()));
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
    public PlayerInventory load(Player player) {
        return new PlayerInventory(
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
}
