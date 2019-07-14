package fr.quatrevieux.araknemu.game.listener.player.exchange;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeParty;
import fr.quatrevieux.araknemu.game.exploration.exchange.event.AcceptChanged;
import fr.quatrevieux.araknemu.game.exploration.exchange.event.ItemMoved;
import fr.quatrevieux.araknemu.game.exploration.exchange.event.KamasChanged;
import fr.quatrevieux.araknemu.game.world.util.Sender;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeAccepted;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.distant.DistantExchangeKamas;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.distant.DistantExchangeObject;

/**
 * Send packets for the distant exchange party
 */
final public class SendDistantPackets implements EventsSubscriber {
    final private Sender output;
    final private ExchangeParty distant;

    public SendDistantPackets(Sender output, ExchangeParty distant) {
        this.output = output;
        this.distant = distant;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<KamasChanged>() {
                @Override
                public void on(KamasChanged event) {
                    output.send(new DistantExchangeKamas(event.quantity()));
                }

                @Override
                public Class<KamasChanged> event() {
                    return KamasChanged.class;
                }
            },
            new Listener<ItemMoved>() {
                @Override
                public void on(ItemMoved event) {
                    output.send(new DistantExchangeObject(event.entry(), event.quantity()));
                }

                @Override
                public Class<ItemMoved> event() {
                    return ItemMoved.class;
                }
            },
            new Listener<AcceptChanged>() {
                @Override
                public void on(AcceptChanged event) {
                    output.send(new ExchangeAccepted(event.accepted(), distant.actor()));
                }

                @Override
                public Class<AcceptChanged> event() {
                    return AcceptChanged.class;
                }
            },
        };
    }
}
