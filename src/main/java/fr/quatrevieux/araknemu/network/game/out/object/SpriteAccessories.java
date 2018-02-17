package fr.quatrevieux.araknemu.network.game.out.object;

import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessories;

/**
 * Change the sprite accessories
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Items.as#L51
 */
final public class SpriteAccessories {
    final private int spriteId;
    final private Accessories accessories;

    public SpriteAccessories(int spriteId, Accessories accessories) {
        this.spriteId = spriteId;
        this.accessories = accessories;
    }

    @Override
    public String toString() {
        return "Oa" + spriteId + "|" + accessories;
    }
}
