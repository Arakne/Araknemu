package fr.quatrevieux.araknemu.game.exploration.map.event;

import fr.quatrevieux.araknemu.game.world.creature.Sprite;

/**
 * Event sent when a new sprite is added
 */
final public class NewSpriteOnMap {
    final private Sprite sprite;

    public NewSpriteOnMap(Sprite sprite) {
        this.sprite = sprite;
    }

    public Sprite sprite() {
        return sprite;
    }
}
