package fr.quatrevieux.araknemu.game.fight.map.util;

import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.util.RandomUtil;

import java.util.List;

/**
 * Generate placement cells on join fight
 */
final public class PlacementCellsGenerator {
    final private FightMap map;
    final private List<Integer> available;
    final private RandomUtil random;

    private int number = -1;

    /**
     * Construct the generator
     *
     * @param map The fight map
     * @param available List of placement cells
     */
    public PlacementCellsGenerator(FightMap map, List<Integer> available) {
        this(map, available, new RandomUtil());
    }

    private PlacementCellsGenerator(FightMap map, List<Integer> available, RandomUtil random) {
        this.map = map;
        this.available = available;
        this.random = random;
    }

    /**
     * Get the next placement cell
     * The next cell is free and walkable cell
     *
     * If there is no more available start place, a random cell will be taken from the entire map
     */
    public FightCell next() {
        if (number >= available.size() - 1) {
            return randomFightCell();
        }

        return nextAvailableCell();
    }

    /**
     * Returns the next available start cell
     */
    private FightCell nextAvailableCell() {
        FightCell cell = map.get(available.get(++number));

        if (cell.walkable()) {
            return cell;
        }

        return next();
    }

    /**
     * Get a random cell from the entire map
     */
    private FightCell randomFightCell() {
        for (;;) {
            FightCell cell = map.get(random.integer(map.size()));

            if (cell.walkable()) {
                return cell;
            }
        }
    }

    /**
     * Randomize the available cells for the placement
     *
     * @param map The fight map
     * @param available The available cells
     */
    static public PlacementCellsGenerator randomized(FightMap map, List<Integer> available) {
        RandomUtil random = new RandomUtil();

        return new PlacementCellsGenerator(map, random.shuffle(available), random);
    }
}
