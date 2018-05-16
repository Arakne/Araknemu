package fr.quatrevieux.araknemu.game.spell.effect.area;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.world.map.GameMap;
import fr.quatrevieux.araknemu.game.world.map.MapCell;
import fr.quatrevieux.araknemu.game.world.map.util.CoordinateCell;

import java.util.HashSet;
import java.util.Set;

/**
 * Resolve circle area
 */
final public class CircleArea implements SpellEffectArea {
    final private EffectArea area;

    public CircleArea(EffectArea area) {
        this.area = area;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends MapCell> Set<C> resolve(C target, C source) {
        Set<C> cells = new HashSet<>(2 * size() * (size() + 1) + 1);

        GameMap<C> map = target.map();
        CoordinateCell<C> center = new CoordinateCell<>(target);

        for (int i = 0; i < map.size(); ++i) {
            CoordinateCell<C> cell = new CoordinateCell<>(map.get(i));

            if (center.distance(cell) <= size()) {
                cells.add(cell.cell());
            }
        }

        return cells;
    }

    @Override
    public EffectArea.Type type() {
        return EffectArea.Type.CIRCLE;
    }

    @Override
    public int size() {
        return area.size();
    }
}
