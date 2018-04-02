package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.player.sprite.AbstractPlayerSpriteInfo;
import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessories;

/**
 * Sprite info for account character
 */
final public class AccountCharacterSpriteInfo extends AbstractPlayerSpriteInfo {
    final private Accessories accessories;

    public AccountCharacterSpriteInfo(Player entity, Accessories accessories) {
        super(entity);
        this.accessories = accessories;
    }

    @Override
    public Accessories accessories() {
        return accessories;
    }
}
