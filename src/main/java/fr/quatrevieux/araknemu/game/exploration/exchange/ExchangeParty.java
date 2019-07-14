package fr.quatrevieux.araknemu.game.exploration.exchange;

import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.world.creature.Creature;
import fr.quatrevieux.araknemu.game.world.util.Sender;

/**
 * Party of an exchange
 */
public interface ExchangeParty extends Sender {
    /**
     * The exchange type
     */
    public ExchangeType type();

    /**
     * Get the party actor
     */
    public Creature actor();

    /**
     * Start the exchange (will start an interaction)
     */
    public void start();

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
     * @param entry The item to move
     * @param quantity The quantity. May be negative for remove item
     */
    public void item(ItemEntry entry, int quantity);
}
