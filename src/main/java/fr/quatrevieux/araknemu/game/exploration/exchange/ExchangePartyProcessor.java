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

import fr.quatrevieux.araknemu.game.item.Item;
import org.checkerframework.checker.index.qual.Positive;

/**
 * Processor for an exchange party
 */
public interface ExchangePartyProcessor {
    /**
     * Check the kamas and items quantity before process the exchange
     *
     * If those quantities are not valid, there will be updated, and the method will return false
     *
     * @return true if the exchange is valid, false otherwise
     */
    public boolean validate();

    /**
     * Process the kamas and items exchange for the current party
     *
     * @param distant The distant party
     */
    public void process(ExchangePartyProcessor distant);

    /**
     * Stop the exchange interaction
     *
     * @param accepted true is the exchange is accepted by both parties, or false for cancel an exchange
     */
    public void terminate(boolean accepted);

    /**
     * Check if the party has accept the exchange
     */
    public boolean accepted();

    /**
     * Reset the accept state of the exchange party
     */
    public void resetAccept();

    /**
     * Add kamas to the party
     *
     * @param kamas The amount of kamas. Must be positive
     */
    public void addKamas(@Positive long kamas);

    /**
     * Add an item to the party
     *
     * @param item The item
     * @param quantity The quantity. Must be positive
     */
    public void addItem(Item item, @Positive int quantity);
}
