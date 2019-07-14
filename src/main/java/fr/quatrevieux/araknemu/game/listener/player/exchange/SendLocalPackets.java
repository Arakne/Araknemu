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
final public class SendLocalPackets implements EventsSubscriber {
    final private ExchangeParty party;

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
