package fr.quatrevieux.araknemu.game.spell.effect.area;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.world.map.GameMap;
import fr.quatrevieux.araknemu.game.world.map.MapCell;
import fr.quatrevieux.araknemu.game.world.map.util.CoordinateCell;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Area with circular form around center cell
 */
final public class CircularArea implements SpellEffectArea {
    final private EffectArea area;
    final private Predicate<Integer> distanceChecker;

    public CircularArea(EffectArea area, Predicate<Integer> distanceChecker) {
        this.area = area;
        this.distanceChecker = distanceChecker;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends MapCell> Set<C> resolve(C target, C source) {
        Set<C> cells = new HashSet<>();

        GameMap<C> map = target.map();
        CoordinateCell<C> center = new CoordinateCell<>(target);

        for (int i = 0; i < map.size(); ++i) {
            CoordinateCell<C> cell = new CoordinateCell<>(map.get(i));

            if (distanceChecker.test(center.distance(cell))) {
                cells.add(cell.cell());
            }
        }

        return cells;
    }

    @Override
    public EffectArea.Type type() {
        return area.type();
    }

    @Override
    public int size() {
        return area.size();
    }
}
