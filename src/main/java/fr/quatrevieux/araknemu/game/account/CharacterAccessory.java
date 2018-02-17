package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessory;
import fr.quatrevieux.araknemu.game.world.creature.accessory.AccessoryType;

/**
 * Adapt simple character item to accessory
 */
final public class CharacterAccessory implements Accessory {
    final private PlayerItem item;

    public CharacterAccessory(PlayerItem item) {
        this.item = item;
    }

    @Override
    public AccessoryType type() {
        return AccessoryType.bySlot(item.position());
    }

    @Override
    public int appearance() {
        return item.itemTemplateId();
    }

    @Override
    public String toString() {
        return Integer.toHexString(appearance());
    }
}
