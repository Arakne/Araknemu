package fr.quatrevieux.araknemu.game.exploration.exchange;

import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.event.AcceptChanged;
import fr.quatrevieux.araknemu.game.exploration.exchange.event.ItemMoved;
import fr.quatrevieux.araknemu.game.exploration.exchange.event.KamasChanged;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeDialog;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.listener.player.exchange.SendDistantPackets;
import fr.quatrevieux.araknemu.game.listener.player.exchange.SendLocalPackets;
import fr.quatrevieux.araknemu.game.world.creature.Creature;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeLeaved;

import java.util.HashMap;
import java.util.Map;

/**
 * Party for player exchange
 *
 * The party is linked to the distant party (other)
 */
final public class PlayerExchangeParty implements ExchangeParty {
    final private ExplorationPlayer player;
    final private ListenerAggregate dispatcher = new DefaultListenerAggregate();

    private PlayerExchangeParty other;

    /**
     * Synchronization object
     * The instance is shared between parties to provide secure synchronization
     * All modifier methods must be synchronized with this instance
     */
    private Object lock;

    /**
     * Accept state of the current party
     */
    volatile private boolean accepted = false;

    final private Map<ItemEntry, Integer> items = new HashMap<>();
    private long kamas;

    public PlayerExchangeParty(ExplorationPlayer player) {
        this.player = player;

        dispatcher.register(new SendLocalPackets(this));
    }

    @Override
    public ExchangeType type() {
        return ExchangeType.PLAYER_EXCHANGE;
    }

    @Override
    public Creature actor() {
        return player;
    }

    @Override
    public void start() {
        player.interactions().start(new ExchangeDialog(this));
    }

    @Override
    public void leave() {
        synchronized (lock) {
            this.stop(false);
            other.stop(false);
        }
    }

    @Override
    public void toggleAccept() {
        synchronized (lock) {
            assertNotAccepted();

            accepted = !accepted;

            if (accepted && other.accepted) {
                process();
            } else {
                dispatcher.dispatch(new AcceptChanged(accepted));
            }
        }
    }

    @Override
    public void kamas(long quantity) {
        synchronized (lock) {
            assertNotAccepted();

            if (quantity < 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }

            resetAccept();

            kamas = Math.min(quantity, player.inventory().kamas());
            dispatcher.dispatch(new KamasChanged(kamas));
        }
    }

    @Override
    public void item(ItemEntry entry, int quantity) {
        synchronized (lock) {
            assertNotAccepted();

            int newQuantity = Math.min(
                items.getOrDefault(entry, 0) + quantity,
                entry.quantity()
            );

            resetAccept();

            if (newQuantity <= 0) {
                items.remove(entry);
            } else {
                items.put(entry, newQuantity);
            }

            dispatcher.dispatch(new ItemMoved(entry, Math.max(0, newQuantity)));
        }
    }

    @Override
    public void send(Object packet) {
        player.send(packet);
    }

    /**
     * Link the distant party to the current one
     */
    private void link(PlayerExchangeParty other) {
        if (lock == null) {
            lock = new Object();
            other.lock = lock;
        }

        this.other = other;
        other.dispatcher.register(new SendDistantPackets(player, other));
    }

    /**
     * Internal stop action
     */
    private void stop(boolean accepted) {
        player.interactions().remove();
        send(new ExchangeLeaved(accepted));
    }

    /**
     * Process the exchange for the two parties
     */
    private void process() {
        // Exchange is not valid : clean the exchange and reset the accept
        // Use boolean operator | instead of logical one ||
        // to ensure that validateExchange is called on both parties
        if (!this.validateExchange() | !other.validateExchange()) {
            resetAccept();
            return;
        }

        this.performExchange();
        other.performExchange();

        this.stop(true);
        other.stop(true);
    }

    /**
     * Perform the kamas and items exchange for the current party
     */
    private void performExchange() {
        if (kamas > 0) {
            player.inventory().removeKamas(kamas);
            other.player.inventory().addKamas(kamas);
        }

        items.forEach((entry, quantity) -> {
            if (quantity > 0) {
                entry.remove(quantity);
                other.player.inventory().add(entry.item(), quantity);
            }
        });
    }

    /**
     * Unset the acceptation for the current party
     */
    private void unsetAccepted() {
        if (!accepted) {
            return;
        }

        accepted = false;
        dispatcher.dispatch(new AcceptChanged(accepted));
    }

    /**
     * Reset parties acceptations on change
     */
    private void resetAccept() {
        this.unsetAccepted();
        other.unsetAccepted();
    }

    /**
     * Check if the exchange is accepted by both parties
     */
    private boolean accepted() {
        return accepted && other.accepted;
    }

    /**
     * Ensure that the exchange is not accepted
     */
    private void assertNotAccepted() {
        if (accepted()) {
            throw new IllegalStateException("Exchange is already accepted");
        }
    }

    /**
     * Check the kamas and items quantity before process the exchange
     *
     * If those quantities are not valid, there will be updated, and the method will return false
     *
     * @return true if the exchange is valid, false otherwise
     */
    private boolean validateExchange() {
        boolean valid = true;

        if (kamas > player.inventory().kamas()) {
            kamas = player.inventory().kamas();
            dispatcher.dispatch(new KamasChanged(kamas));

            valid = false;
        }

        for (Map.Entry<ItemEntry, Integer> entry : items.entrySet()) {
            if (entry.getValue() > entry.getKey().quantity()) {
                entry.setValue(entry.getKey().quantity());
                dispatcher.dispatch(new ItemMoved(entry.getKey(), entry.getValue()));

                valid = false;
            }
        }

        return valid;
    }

    /**
     * Make exchange parties between two players
     */
    static public PlayerExchangeParty[] make(ExplorationPlayer player1, ExplorationPlayer player2) {
        final PlayerExchangeParty party1 = new PlayerExchangeParty(player1);
        final PlayerExchangeParty party2 = new PlayerExchangeParty(player2);

        party1.link(party2);
        party2.link(party1);

        return new PlayerExchangeParty[] {party1, party2};
    }
}
