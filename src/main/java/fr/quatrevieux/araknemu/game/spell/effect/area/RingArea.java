package fr.quatrevieux.araknemu.game.spell.effect.area;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.world.map.GameMap;
import fr.quatrevieux.araknemu.game.world.map.MapCell;
import fr.quatrevieux.araknemu.game.world.map.util.CoordinateCell;

import java.util.HashSet;
import java.util.Set;

/**
 * Resolve ring area (circle border)
 */
final public class RingArea implements SpellEffectArea {
    final private EffectArea area;

    public RingArea(EffectArea area) {
        this.area = area;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends MapCell> Set<C> resolve(C target, C source) {
        Set<C> cells = new HashSet<>();

        GameMap<C> map = target.map();
        CoordinateCell<C> center = new CoordinateCell<>(target);

        for (int i = 0; i < map.size(); ++i) {
            CoordinateCell<C> cell = new CoordinateCell<>(map.get(i));

            if (center.distance(cell) == area.size()) {
                cells.add(cell.cell());
            }
        }

        return cells;
    }

    @Override
    public EffectArea.Type type() {
        return EffectArea.Type.RING;
    }

    @Override
    public int size() {
        return area.size();
    }
}
