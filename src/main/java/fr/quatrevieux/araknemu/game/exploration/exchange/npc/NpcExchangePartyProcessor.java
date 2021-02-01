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

package fr.quatrevieux.araknemu.game.exploration.exchange.npc;

import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangePartyProcessor;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeStorage;
import fr.quatrevieux.araknemu.game.exploration.exchange.event.AcceptChanged;
import fr.quatrevieux.araknemu.game.exploration.exchange.event.ExchangeEvent;
import fr.quatrevieux.araknemu.game.exploration.exchange.event.ItemMoved;
import fr.quatrevieux.araknemu.game.exploration.exchange.event.KamasChanged;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.exploration.npc.exchange.GameNpcExchange;
import fr.quatrevieux.araknemu.game.exploration.npc.exchange.NpcExchangeEntry;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;

import java.util.HashMap;
import java.util.Map;

/**
 * Party processor for an Npc
 *
 * - Listen for player exchange changes for find the matching exchange entry
 * - When a matching exchange is found, notify the new exchange entry
 * - When the player accept the exchange, generates items and process the exchange
 */
public final class NpcExchangePartyProcessor implements ExchangePartyProcessor, EventsSubscriber {
    /**
     * Store the current Npc exchange state
     * The storage is immutable to permit check changes
     */
    private class Storage implements ExchangeStorage {
        private final NpcExchangeEntry entry;
        private final Map<ItemEntry, Integer> items;

        public Storage(NpcExchangeEntry entry) {
            this.entry = entry;
            this.items = buildItems(entry);
        }

        @Override
        public Map<ItemEntry, Integer> items() {
            return items;
        }

        @Override
        public long kamas() {
            return entry.kamas();
        }

        @Override
        public boolean accepted() {
            return entry.valid();
        }

        @Override
        public ListenerAggregate dispatcher() {
            return dispatcher;
        }

        @Override
        public ExplorationCreature owner() {
            return npc;
        }

        /**
         * Notify all changes on the exchange
         *
         * @param old The old exchange storage state
         */
        private void notifyChanges(Storage old) {
            if (kamas() != old.kamas()) {
                dispatcher.dispatch(new KamasChanged(kamas(), this));
            }

            if (!items().equals(old.items())) {
                old.items().keySet().forEach((entry) -> dispatcher.dispatch(new ItemMoved(entry, 0, this)));
                items().forEach((entry, quantity) -> dispatcher.dispatch(new ItemMoved(entry, quantity, this)));
            }

            if (accepted() != old.accepted()) {
                dispatcher.dispatch(new AcceptChanged(accepted(), this));
            }
        }

        private Map<ItemEntry, Integer> buildItems(NpcExchangeEntry entry) {
            final Map<ItemEntry, Integer> items = new HashMap<>();

            for (Map.Entry<ItemTemplate, Integer> item : entry.items()) {
                items.put(new NpcExchangeItemEntry(item.getKey()), item.getValue());
            }

            return items;
        }
    }

    private final GameNpc npc;
    private final GameNpcExchange exchange;
    private final ListenerAggregate dispatcher = new DefaultListenerAggregate();

    private Storage storage;

    public NpcExchangePartyProcessor(GameNpc npc, GameNpcExchange exchange) {
        this.npc = npc;
        this.exchange = exchange;
        this.storage = new Storage(NpcExchangeEntry.NULL_ENTRY);
    }

    @Override
    public boolean accepted() {
        return storage.accepted();
    }

    @Override
    public boolean validate() {
        return storage.accepted();
    }

    @Override
    public void process(ExchangePartyProcessor distant) {
        storage.entry.generate().forEach(distant::addItem);

        if (storage.entry.kamas() > 0) {
            distant.addKamas(storage.entry.kamas());
        }
    }

    @Override
    public void terminate(boolean accepted) {
        // No-op
    }

    @Override
    public void resetAccept() {
        // No-op
    }

    @Override
    public void addKamas(long kamas) {
        // No-op
    }

    @Override
    public void addItem(Item item, int quantity) {
        // No-op
    }

    /**
     * Listen for changes on the player exchange
     */
    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<ItemMoved>() {
                @Override
                public void on(ItemMoved event) {
                    updateExchange(event);
                }

                @Override
                public Class<ItemMoved> event() {
                    return ItemMoved.class;
                }
            },
            new Listener<KamasChanged>() {
                @Override
                public void on(KamasChanged event) {
                    updateExchange(event);
                }

                @Override
                public Class<KamasChanged> event() {
                    return KamasChanged.class;
                }
            },
        };
    }

    ListenerAggregate dispatcher() {
        return dispatcher;
    }

    /**
     * Check the player exchange items and kamas for find the corresponding exchange
     */
    private void updateExchange(ExchangeEvent event) {
        final Storage last = storage;

        // Find matching exchange and update the exchange
        storage = new Storage(exchange.find(event.storage().items(), event.storage().kamas()));
        storage.notifyChanges(last);
    }
}
