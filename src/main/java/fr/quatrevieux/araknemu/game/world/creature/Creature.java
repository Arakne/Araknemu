package fr.quatrevieux.araknemu.game.world.creature;

import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.world.map.Direction;

/**
 * Base interface for Dofus creatures (monsters, players...)
 *
 * @todo parametrize for the operation type (ExplorationCreatureOperation / FightCreatureOperation)
 */
public interface Creature {
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
    public ExplorationMapCell cell();

    /**
     * Get the creature orientation
     */
    public Direction orientation();

    /**
     * Apply the operation on the creature
     */
    public void apply(Operation operation);
}
