package fr.quatrevieux.araknemu.game.fight.map.util;

import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Generate placement cells on join fight
 */
final public class PlacementCellsGenerator {
    final static private Random RANDOM = new Random();

    final private FightMap map;
    final private List<Integer> available;

    private int number = -1;

    public PlacementCellsGenerator(FightMap map, List<Integer> available) {
        this.map = map;
        this.available = available;
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
            FightCell cell = map.get(RANDOM.nextInt(map.size()));

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
        available = new ArrayList<>(available);
        Collections.shuffle(available);

        return new PlacementCellsGenerator(map, available);
    }
}
