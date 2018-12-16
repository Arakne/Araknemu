package fr.quatrevieux.araknemu.game.listener.player.inventory;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.characteristic.event.CharacteristicsChanged;
import fr.quatrevieux.araknemu.game.player.event.GameJoined;
import fr.quatrevieux.araknemu.game.player.inventory.event.ObjectAdded;
import fr.quatrevieux.araknemu.game.player.inventory.event.ObjectDeleted;
import fr.quatrevieux.araknemu.game.player.inventory.event.ObjectQuantityChanged;
import fr.quatrevieux.araknemu.network.game.out.object.InventoryWeight;

/**
 * Send inventory weight when inventory operations or characteristics changes occurs
 */
final public class SendWeight implements EventsSubscriber {
    final private GamePlayer player;

    public SendWeight(GamePlayer player) {
        this.player = player;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<GameJoined>() {
                @Override
                public void on(GameJoined event) {
                    send();
                }

                @Override
                public Class<GameJoined> event() {
                    return GameJoined.class;
                }
            },
            new Listener<CharacteristicsChanged>() {
                @Override
                public void on(CharacteristicsChanged event) {
                    send();
                }

                @Override
                public Class<CharacteristicsChanged> event() {
                    return CharacteristicsChanged.class;
                }
            },
            new Listener<ObjectQuantityChanged>() {
                @Override
                public void on(ObjectQuantityChanged event) {
                    send();
                }

                @Override
                public Class<ObjectQuantityChanged> event() {
                    return ObjectQuantityChanged.class;
                }
            },
            new Listener<ObjectAdded>() {
                @Override
                public void on(ObjectAdded event) {
                    send();
                }

                @Override
                public Class<ObjectAdded> event() {
                    return ObjectAdded.class;
                }
            },
            new Listener<ObjectDeleted>() {
                @Override
                public void on(ObjectDeleted event) {
                    send();
                }

                @Override
                public Class<ObjectDeleted> event() {
                    return ObjectDeleted.class;
                }
            },
        };
    }

    private void send() {
        player.inventory().refreshWeight();
        player.send(new InventoryWeight(player));
    }
}
