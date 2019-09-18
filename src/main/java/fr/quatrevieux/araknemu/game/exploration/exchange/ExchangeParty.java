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

package fr.quatrevieux.araknemu.game.exploration.exchange;

import fr.quatrevieux.araknemu.game.world.creature.Creature;
import fr.quatrevieux.araknemu.game.world.util.Sender;

/**
 * Party of an exchange
 */
public interface ExchangeParty extends Exchange, Sender {
    /**
     * The exchange type
     */
    public ExchangeType type();

    /**
     * Get the party actor
     */
    public Creature actor();

    /**
     * Get the target creature of the exchange
     *
     * @return The creature, or null if there is no other party on the exchange
     */
    public Creature target();

    /**
     * Leave / cancel the exchange (will remove the interaction)
     */
    public void leave();

    /**
     * Toggle the accept state of the party
     */
    public void toggleAccept();

    /**
     * Add (or set) the kamas quantity
     *
     * @param quantity The kamas quantity. May be negative for remove kamas
     */
    public void kamas(long quantity);

    /**
     * Add or remove an item on the exchange
     *
     * @param itemEntryId The id of item to move
     * @param quantity The quantity. May be negative for remove item
     */
    public void item(int itemEntryId, int quantity);
}
