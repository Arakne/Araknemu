package fr.quatrevieux.araknemu.game.player.sprite;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.player.inventory.PlayerInventory;
import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessories;

/**
 * Sprite info for game player
 */
final public class GamePlayerSpriteInfo extends AbstractPlayerSpriteInfo {
    final private PlayerInventory inventory;

    public GamePlayerSpriteInfo(Player player, PlayerInventory inventory) {
        super(player);

        this.inventory = inventory;
    }

    @Override
    public Accessories accessories() {
        return inventory.accessories();
    }
}
