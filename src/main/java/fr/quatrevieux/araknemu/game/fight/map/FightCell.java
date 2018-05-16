package fr.quatrevieux.araknemu.game.fight.map;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.world.map.MapCell;

import java.util.Optional;

/**
 * Cell for a fight map
 */
public interface FightCell extends MapCell {
    @Override
    public FightMap map();

    /**
     * Check if the cell is walkable, ignoring current fighter
     */
    public boolean walkableIgnoreFighter();

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
