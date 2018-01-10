package fr.quatrevieux.araknemu.network.game.out.game;

import fr.quatrevieux.araknemu.game.world.creature.Sprite;

/**
 * Remove a sprite from the current map
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L434
 */
final public class RemoveSprite {
    final private Sprite sprite;

    public RemoveSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public String toString() {
        return "GM|-" + sprite.id();
    }
}
