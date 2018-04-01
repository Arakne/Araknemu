package fr.quatrevieux.araknemu.game.player.inventory.slot.constraint;

import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.BadLevelException;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;

/**
 * Check the player level for equip the item
 */
final public class EquipmentLevelConstraint implements SlotConstraint {
    final private GamePlayer player;

    public EquipmentLevelConstraint(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void check(Item item, int quantity) throws InventoryException {
        if (item.template().level() > player.experience().level()) {
            throw new BadLevelException(item.template().level());
        }
    }
}
