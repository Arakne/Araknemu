package fr.quatrevieux.araknemu.game.exploration.map.event;

import fr.quatrevieux.araknemu.game.world.creature.Sprite;

/**
 * A sprite is removed from the map
 */
final public class SpriteRemoveFromMap {
    final private Sprite sprite;

    public SpriteRemoveFromMap(Sprite sprite) {
        this.sprite = sprite;
    }

    public Sprite sprite() {
        return sprite;
    }
}
