package fr.quatrevieux.araknemu.game.spell.effect.area;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.game.world.map.MapCell;

import java.util.HashSet;
import java.util.Set;

/**
 * Resolver for cross area
 */
final public class CrossArea implements SpellEffectArea {
    final private EffectArea area;

    public CrossArea(EffectArea area) {
        this.area = area;
    }

    @Override
    public <C extends MapCell> Set<C> resolve(C target, C source) {
        Set<C> cells = new HashSet<>(area.size() * 4 + 1);

        cells.add(target);

        for (Direction direction : Direction.values()) {
            if (!direction.restricted()) {
                continue;
            }

            LineArea.addCells(cells, target, direction, area.size());
        }

        return cells;
    }

    @Override
    public EffectArea.Type type() {
        return EffectArea.Type.CROSS;
    }

    @Override
    public int size() {
        return area.size();
    }
}
