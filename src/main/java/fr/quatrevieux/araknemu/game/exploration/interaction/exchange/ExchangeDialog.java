package fr.quatrevieux.araknemu.game.exploration.interaction.exchange;

import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeParty;
import fr.quatrevieux.araknemu.game.exploration.interaction.Accaptable;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
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
        exchange.send(new ExchangeCreated(exchange.type()));

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
    public void item(ItemEntry item, int quantity) {
        exchange.item(item, quantity);
    }
}
