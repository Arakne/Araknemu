package fr.quatrevieux.araknemu.game.listener.player.exchange;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.item.inventory.event.KamasChanged;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectAdded;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectDeleted;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectQuantityChanged;
import fr.quatrevieux.araknemu.game.world.util.Sender;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.storage.StorageKamas;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.storage.StorageObject;

/**
 * Send packets for storage's change
 */
final public class SendStoragePackets implements EventsSubscriber {
    final private Sender output;

    public SendStoragePackets(Sender output) {
        this.output = output;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<ObjectAdded>() {
                @Override
                public void on(ObjectAdded event) {
                    output.send(new StorageObject(event.entry()));
                }

                @Override
                public Class<ObjectAdded> event() {
                    return ObjectAdded.class;
                }
            },

            new Listener<ObjectDeleted>() {
                @Override
                public void on(ObjectDeleted event) {
                    output.send(new StorageObject(event.entry()));
                }

                @Override
                public Class<ObjectDeleted> event() {
                    return ObjectDeleted.class;
                }
            },

            new Listener<ObjectQuantityChanged>() {
                @Override
                public void on(ObjectQuantityChanged event) {
                    output.send(new StorageObject(event.entry()));
                }

                @Override
                public Class<ObjectQuantityChanged> event() {
                    return ObjectQuantityChanged.class;
                }
            },

            new Listener<KamasChanged>() {
                @Override
                public void on(KamasChanged event) {
                    output.send(new StorageKamas(event.newQuantity()));
                }

                @Override
                public Class<KamasChanged> event() {
                    return KamasChanged.class;
                }
            },
        };
    }
}
