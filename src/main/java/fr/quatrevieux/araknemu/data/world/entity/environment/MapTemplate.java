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
        public boolean LineOfSight();

        /**
         * Get the permitted movement type
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
    }

    final private int id;
    final private String date;
    final private Dimensions dimensions;
    final private String key;
    final private List<Cell> cells;

    public MapTemplate(int id, String date, Dimensions dimensions, String key, List<Cell> cells) {
        this.id = id;
        this.date = date;
        this.dimensions = dimensions;
        this.key = key;
        this.cells = cells;
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
}
