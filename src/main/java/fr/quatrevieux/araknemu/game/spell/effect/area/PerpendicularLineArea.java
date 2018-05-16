package fr.quatrevieux.araknemu.game.spell.effect.area;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.game.world.map.MapCell;
import fr.quatrevieux.araknemu.game.world.map.util.CoordinateCell;

import java.util.HashSet;
import java.util.Set;

/**
 * Resolve perpendicular line (i.e. baton area)
 */
final public class PerpendicularLineArea implements SpellEffectArea {
    final private EffectArea area;

    public PerpendicularLineArea(EffectArea area) {
        this.area = area;
    }

    @Override
    public <C extends MapCell> Set<C> resolve(C target, C source) {
        Set<C> cells = new HashSet<>(area.size() * 2 + 1);

        Direction direction = new CoordinateCell<>(source).directionTo(new CoordinateCell<>(target)).orthogonal();

        cells.add(target);

        LineArea.addCells(cells, target, direction, area.size());
        LineArea.addCells(cells, target, direction.opposite(), area.size());

        return cells;
    }

    @Override
    public EffectArea.Type type() {
        return EffectArea.Type.PERPENDICULAR_LINE;
    }

    @Override
    public int size() {
        return area.size();
    }
}
