package fr.quatrevieux.araknemu.game.event.listener.map;

import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.inventory.EquipmentChanged;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.world.creature.accessory.AccessoryType;
import fr.quatrevieux.araknemu.network.game.out.object.SpriteAccessories;

/**
 * Send accessories when an equipment is changed
 */
final public class SendAccessories implements Listener<EquipmentChanged> {
    final private ExplorationPlayer player;

    public SendAccessories(ExplorationPlayer player) {
        this.player = player;
    }

    @Override
    public void on(EquipmentChanged event) {
        if (!AccessoryType.isAccessorySlot(event.slot()) && !AccessoryType.isAccessorySlot(event.entry().position())) {
            return;
        }

        player.map().send(
            new SpriteAccessories(
                player.id(),
                player.inventory().accessories()
            )
        );
    }

    @Override
    public Class<EquipmentChanged> event() {
        return EquipmentChanged.class;
    }
}
