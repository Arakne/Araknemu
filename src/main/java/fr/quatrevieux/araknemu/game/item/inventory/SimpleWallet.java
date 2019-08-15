package fr.quatrevieux.araknemu.game.item.inventory;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.data.living.entity.WalletEntity;
import fr.quatrevieux.araknemu.game.item.inventory.event.KamasChanged;

/**
 * Base implementation of Wallet
 */
final public class SimpleWallet implements Wallet {
    final private WalletEntity entity;
    final private Dispatcher dispatcher;

    public SimpleWallet(WalletEntity entity, Dispatcher dispatcher) {
        this.entity = entity;
        this.dispatcher = dispatcher;
    }

    @Override
    public long kamas() {
        return entity.kamas();
    }

    @Override
    public void addKamas(long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be a positive number");
        }

        updateKamas(quantity);
    }

    @Override
    public void removeKamas(long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be a positive number");
        }

        if (quantity > entity.kamas()) {
            throw new IllegalArgumentException("Quantity is too high");
        }

        updateKamas(-quantity);
    }

    /**
     * Update quantity of kamas, and trigger {@link KamasChanged} event
     *
     * @param quantity The change quantity. Positive for adding, negative for remove
     */
    private void updateKamas(long quantity) {
        final long last = entity.kamas();

        entity.setKamas(last + quantity);
        dispatcher.dispatch(new KamasChanged(last, entity.kamas()));
    }
}
