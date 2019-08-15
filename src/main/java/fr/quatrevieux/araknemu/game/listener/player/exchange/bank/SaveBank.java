package fr.quatrevieux.araknemu.game.listener.player.exchange.bank;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.living.repository.account.BankItemRepository;
import fr.quatrevieux.araknemu.game.account.bank.BankEntry;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectAdded;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectDeleted;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectQuantityChanged;

/**
 * Save the bank on changes
 */
final public class SaveBank implements EventsSubscriber {
    final private BankItemRepository repository;

    public SaveBank(BankItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<ObjectAdded>() {
                @Override
                public void on(ObjectAdded event) {
                    if (!(event.entry() instanceof BankEntry)) {
                        return;
                    }

                    BankEntry entry = (BankEntry) event.entry();

                    repository.add(entry.entity());
                }

                @Override
                public Class<ObjectAdded> event() {
                    return ObjectAdded.class;
                }
            },

            new Listener<ObjectDeleted>() {
                @Override
                public void on(ObjectDeleted event) {
                    if (!(event.entry() instanceof BankEntry)) {
                        return;
                    }

                    BankEntry entry = (BankEntry) event.entry();

                    repository.delete(entry.entity());
                }

                @Override
                public Class<ObjectDeleted> event() {
                    return ObjectDeleted.class;
                }
            },

            new Listener<ObjectQuantityChanged>() {
                @Override
                public void on(ObjectQuantityChanged event) {
                    if (!(event.entry() instanceof BankEntry)) {
                        return;
                    }

                    BankEntry entry = (BankEntry) event.entry();

                    repository.update(entry.entity());
                }

                @Override
                public Class<ObjectQuantityChanged> event() {
                    return ObjectQuantityChanged.class;
                }
            },
        };
    }
}
