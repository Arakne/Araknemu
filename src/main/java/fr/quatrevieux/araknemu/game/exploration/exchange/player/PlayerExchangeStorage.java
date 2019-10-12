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

package fr.quatrevieux.araknemu.game.exploration.exchange.player;

import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeStorage;
import fr.quatrevieux.araknemu.game.exploration.exchange.event.AcceptChanged;
import fr.quatrevieux.araknemu.game.exploration.exchange.event.ItemMoved;
import fr.quatrevieux.araknemu.game.exploration.exchange.event.KamasChanged;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Store the pending exchange for a player
 */
final public class PlayerExchangeStorage implements ExchangeStorage {
    final private ExplorationPlayer player;
    final private ListenerAggregate dispatcher = new DefaultListenerAggregate();

    final private Map<ItemEntry, Integer> items = new HashMap<>();
    private long kamas = 0;
    volatile private boolean accepted = false;

    public PlayerExchangeStorage(ExplorationPlayer player) {
        this.player = player;
    }

    @Override
    public Map<ItemEntry, Integer> items() {
        return Collections.unmodifiableMap(items);
    }

    @Override
    public long kamas() {
        return kamas;
    }

    @Override
    public boolean accepted() {
        return accepted;
    }

    @Override
    public ListenerAggregate dispatcher() {
        return dispatcher;
    }

    @Override
    public ExplorationCreature owner() {
        return player;
    }

    /**
     * Get the current quantity of the item in the exchange
     */
    public int quantity(ItemEntry item) {
        return items.getOrDefault(item, 0);
    }

    /**
     * Set an item to the exchange
     *
     * @param item The item to exchange
     * @param quantity The quantity
     */
    public void setItem(ItemEntry item, int quantity) {
        if (quantity <= 0) {
            items.remove(item);
        } else {
            items.put(item, quantity);
        }

        dispatcher.dispatch(new ItemMoved(item, Math.max(0, quantity), this));
    }

    /**
     * Set kamas to the exchange
     *
     * @param kamas The kamas quantity
     */
    public void setKamas(long kamas) {
        this.kamas = kamas;
        dispatcher.dispatch(new KamasChanged(kamas, this));
    }

    /**
     * Change the exchange accept state
     */
    public void setAccepted(boolean accepted) {
        if (this.accepted == accepted) {
            return;
        }

        this.accepted = accepted;
        dispatcher.dispatch(new AcceptChanged(accepted, this));
    }
}
