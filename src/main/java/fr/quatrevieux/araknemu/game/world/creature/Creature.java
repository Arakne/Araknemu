package fr.quatrevieux.araknemu.game.world.creature;

import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.game.world.map.MapCell;

/**
 * Base interface for Dofus creatures (monsters, players...)
 */
public interface Creature<C extends MapCell> {
    /**
     * Get the sprite
     */
    public Sprite sprite();

    /**
     * Get the creature id
     */
    public int id();

    /**
     * Get the creature cell
     */
    public C cell();

    /**
     * Get the creature orientation
     */
    public Direction orientation();
}
