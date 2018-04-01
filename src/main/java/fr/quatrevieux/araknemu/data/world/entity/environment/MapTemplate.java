package fr.quatrevieux.araknemu.data.world.entity.environment;

import fr.quatrevieux.araknemu.data.value.Dimensions;

import java.util.List;

/**
 * Entity for Dofus map
 */
final public class MapTemplate {
    public interface Cell {
        /**
         * Check if the cell do not block the line of sight
         */
        public boolean lineOfSight();

        /**
         * Get the permitted movement type
         *
         * The value is an int in range [0 - 5] :
         *
         * - 0 means not walkable
         * - 1 means walkable, but not on a road
         * - 2 to 5 means different levels of walkable cells. Bigger is the movement, lower is the weight on pathing
         */
        public int movement();

        /**
         * Check if the cell contains an interactive object
         */
        public boolean interactive();

        /**
         * Get the cell object id
         */
        public int objectId();

        /**
         * Check if the cell is active or not
         */
        public boolean active();
    }

    final private int id;
    final private String date;
    final private Dimensions dimensions;
    final private String key;
    final private List<Cell> cells;
    final private List<Integer>[] fightPlaces;

    public MapTemplate(int id, String date, Dimensions dimensions, String key, List<Cell> cells, List<Integer>[] fightPlaces) {
        this.id = id;
        this.date = date;
        this.dimensions = dimensions;
        this.key = key;
        this.cells = cells;
        this.fightPlaces = fightPlaces;
    }

    public int id() {
        return id;
    }

    public String date() {
        return date;
    }

    public Dimensions dimensions() {
        return dimensions;
    }

    public String key() {
        return key;
    }

    public List<Cell> cells() {
        return cells;
    }

    public List<Integer>[] fightPlaces() {
        return fightPlaces;
    }
}
