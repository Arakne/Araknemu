package fr.quatrevieux.araknemu.game.player.sprite;

import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessories;

/**
 * Information and parameters of the player sprite
 */
public interface SpriteInfo {
    /**
     * Get the sprite id
     */
    public int id();

    /**
     * Get the sprite name
     */
    public String name();

    /**
     * Get the sprite colors
     */
    public Colors colors();

    /**
     * Get the sprite gfxId
     */
    public int gfxId();

    /**
     * Get the sprite size
     */
    public SpriteSize size();

    /**
     * Get the sprite accessories
     */
    public Accessories accessories();

    /**
     * Get the player gender
     */
    public Sex sex();

    /**
     * Get the player race
     */
    public Race race();
}
