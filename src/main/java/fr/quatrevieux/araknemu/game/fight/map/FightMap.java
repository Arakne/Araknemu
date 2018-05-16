package fr.quatrevieux.araknemu.game.fight.map;

import fr.quatrevieux.araknemu.data.value.Dimensions;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.game.world.map.GameMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Map for the fight
 */
final public class FightMap implements GameMap<FightCell> {
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
    @Override
    public FightCell get(int cellId) {
        return cells.get(cellId);
    }

    /**
     * Get start places for a team
     */
    public List<Integer> startPlaces(int team) {
        return template.fightPlaces()[team];
    }

    @Override
    public Dimensions dimensions() {
        return template.dimensions();
    }

    @Override
    public int size() {
        return cells.size();
    }

    /**
     * Clear map data
     */
    public void destroy() {
        for (FightCell cell : cells) {
            cell.fighter().ifPresent(fighter -> cell.removeFighter());
        }

        cells.clear();
    }

    private List<FightCell> makeCells(List<MapTemplate.Cell> template) {
        ArrayList<FightCell> cells = new ArrayList<>(template.size());

        for (int i = 0; i < template.size(); ++i) {
            MapTemplate.Cell cell = template.get(i);

            if (!cell.active() || cell.movement() < 2) {
                cells.add(new UnwalkableFightCell(this, template.get(i), i));
            } else {
                cells.add(new WalkableFightCell(this, template.get(i), i));
            }
        }

        return cells;
    }
}
