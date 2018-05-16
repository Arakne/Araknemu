package fr.quatrevieux.araknemu.game.spell.effect.area;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.game.world.map.GameMap;
import fr.quatrevieux.araknemu.game.world.map.MapCell;
import fr.quatrevieux.araknemu.game.world.map.util.CoordinateCell;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Resolve in line area
 */
final public class LineArea implements SpellEffectArea {
    final private EffectArea area;

    public LineArea(EffectArea area) {
        this.area = area;
    }

    @Override
    public <C extends MapCell> Set<C> resolve(C target, C source) {
        if (area.size() == 0) {
            return Collections.singleton(target);
        }

        Direction direction = new CoordinateCell<>(source).directionTo(new CoordinateCell<>(target));

        Set<C> cells = new HashSet<>(area.size() + 1);

        cells.add(target);
        addCells(cells, target, direction, area.size());

        return cells;
    }

    @Override
    public EffectArea.Type type() {
        return EffectArea.Type.LINE;
    }

    @Override
    public int size() {
        return area.size();
    }

    @SuppressWarnings("unchecked")
    static  <C extends MapCell> void addCells(Set<C> cells, C start, Direction direction, int size) {
        GameMap<C> map = start.map();
        int inc = direction.nextCellIncrement(map.dimensions().width());

        CoordinateCell<C> last = new CoordinateCell<>(start);

        for (int i = 0; i < size; ++i) {
            int cellId = last.id() + inc;

            if (cellId < 0 || cellId >= map.size()) {
                break;
            }

            CoordinateCell<C> next = new CoordinateCell<>(map.get(cellId));

            // The next cell is out of the direction
            if (last.directionTo(next) != direction) {
                break;
            }

            cells.add(next.cell());
            last = next;
        }
    }
}
