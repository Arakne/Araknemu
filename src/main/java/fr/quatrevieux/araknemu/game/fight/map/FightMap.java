package fr.quatrevieux.araknemu.game.fight.map;

import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Map for the fight
 */
final public class FightMap {
    final private MapTemplate template;
    final private List<FightCell> cells;

    public FightMap(MapTemplate template) {
        this.template = template;
        this.cells = makeCells(template.cells());
    }

    /**
     * Get a fight cell
     *
     * @param cellId The cell id
     */
    public FightCell get(int cellId) {
        return cells.get(cellId);
    }

    /**
     * Get start places for a team
     */
    public List<Integer> startPlaces(int team) {
        return template.fightPlaces()[team];
    }

    private List<FightCell> makeCells(List<MapTemplate.Cell> template) {
        ArrayList<FightCell> cells = new ArrayList<>(template.size());

        for (int i = 0; i < template.size(); ++i) {
            MapTemplate.Cell cell = template.get(i);

            if (!cell.active() || cell.movement() < 2) {
                cells.add(new UnwalkableFightCell(template.get(i), i));
            } else {
                cells.add(new WalkableFightCell(template.get(i), i));
            }
        }

        return cells;
    }
}
