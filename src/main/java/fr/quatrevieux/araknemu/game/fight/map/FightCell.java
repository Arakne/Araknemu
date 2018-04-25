package fr.quatrevieux.araknemu.game.fight.map;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.world.map.MapCell;

import java.util.Optional;

/**
 * Cell for a fight map
 */
public interface FightCell extends MapCell {
    /**
     * Get the cell number
     */
    public int id();

    /**
     * Check if the cell is walkable
     */
    public boolean walkable();

    /**
     * Check if the cell block line of sight
     */
    public boolean sightBlocking();

    /**
     * Get the fighter on the cell
     */
    public Optional<Fighter> fighter();

    /**
     * Set a fighter on this cell
     */
    public void set(Fighter fighter);

    /**
     * Remove the fighter on the cell
     */
    public void removeFighter();
}
