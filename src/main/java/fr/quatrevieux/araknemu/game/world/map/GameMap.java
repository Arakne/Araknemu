package fr.quatrevieux.araknemu.game.world.map;

import fr.quatrevieux.araknemu.data.value.Dimensions;

/**
 * Base dofus map type
 *
 * @param <C> The cell type
 */
public interface GameMap<C extends MapCell> {
    /**
     * Get the map size (the number of cells)
     */
    public int size();

    /**
     * Get a cell by its id
     *
     * @param id The cell id
     */
    public C get(int id);

    /**
     * Get the map dimensions
     */
    public Dimensions dimensions();
}
