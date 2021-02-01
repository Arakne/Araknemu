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

package fr.quatrevieux.araknemu.game.listener.player.exchange;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeParty;
import fr.quatrevieux.araknemu.game.exploration.exchange.event.AcceptChanged;
import fr.quatrevieux.araknemu.game.exploration.exchange.event.ItemMoved;
import fr.quatrevieux.araknemu.game.exploration.exchange.event.KamasChanged;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeAccepted;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.local.LocalExchangeKamas;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.local.LocalExchangeObject;

/**
 * Send packets for the local exchange
 */
public final class SendLocalPackets implements EventsSubscriber {
    private final ExchangeParty party;

    public SendLocalPackets(ExchangeParty party) {
        this.party = party;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<KamasChanged>() {
                @Override
                public void on(KamasChanged event) {
                    party.send(new LocalExchangeKamas(event.quantity()));
                }

                @Override
                public Class<KamasChanged> event() {
                    return KamasChanged.class;
                }
            },
            new Listener<ItemMoved>() {
                @Override
                public void on(ItemMoved event) {
                    party.send(new LocalExchangeObject(event.entry(), event.quantity()));
                }

                @Override
                public Class<ItemMoved> event() {
                    return ItemMoved.class;
                }
            },
            new Listener<AcceptChanged>() {
                @Override
                public void on(AcceptChanged event) {
                    party.send(new ExchangeAccepted(event.accepted(), party.actor()));
                }

                @Override
                public Class<AcceptChanged> event() {
                    return AcceptChanged.class;
                }
            },
        };
    }
}
