package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.player.inventory.event.EquipmentChanged;
import fr.quatrevieux.araknemu.game.world.creature.accessory.AccessoryType;
import fr.quatrevieux.araknemu.network.game.out.object.SpriteAccessories;

/**
 * Send accessories when an equipment is changed during fight placement
 */
final public class SendFighterAccessories implements Listener<EquipmentChanged> {
    final private PlayerFighter fighter;

    public SendFighterAccessories(PlayerFighter fighter) {
        this.fighter = fighter;
    }

    @Override
    public void on(EquipmentChanged event) {
        if (!AccessoryType.isAccessorySlot(event.slot()) && !AccessoryType.isAccessorySlot(event.entry().position())) {
            return;
        }

        fighter.fight().send(
            new SpriteAccessories(
                fighter.id(),
                fighter.player().inventory().accessories()
            )
        );
    }

    @Override
    public Class<EquipmentChanged> event() {
        return EquipmentChanged.class;
    }
}
