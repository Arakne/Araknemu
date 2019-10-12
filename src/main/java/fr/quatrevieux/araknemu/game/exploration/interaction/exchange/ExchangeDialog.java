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

package fr.quatrevieux.araknemu.game.exploration.interaction.exchange;

import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeParty;
import fr.quatrevieux.araknemu.game.exploration.interaction.Accaptable;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeCreated;

/**
 * The interaction dialog for an exchange
 */
final public class ExchangeDialog implements ExchangeInteraction, Accaptable {
    final private ExchangeParty exchange;

    public ExchangeDialog(ExchangeParty exchange) {
        this.exchange = exchange;
    }

    @Override
    public void leave() {
        exchange.leave();
    }

    @Override
    public Interaction start() {
        exchange.send(new ExchangeCreated(exchange.type(), exchange.target()));
        exchange.initialize();

        return this;
    }

    @Override
    public void stop() {
        exchange.leave();
    }

    @Override
    public void accept() {
        exchange.toggleAccept();
    }

    /**
     * Set the kamas quantity on the exchange
     */
    public void kamas(long quantity) {
        exchange.kamas(quantity);
    }

    /**
     * Set an item on the exchange
     */
    public void item(int itemEntryId, int quantity) {
        exchange.item(itemEntryId, quantity);
    }
}
