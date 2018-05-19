package fr.quatrevieux.araknemu.game.spell.effect.area;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.world.map.MapCell;

import java.util.Set;

/**
 * Resolve circle area
 */
final public class CircleArea implements SpellEffectArea {
    final private CircularArea area;

    public CircleArea(EffectArea area) {
        this.area = new CircularArea(area, distance -> distance <= size());
    }

    @Override
    public <C extends MapCell> Set<C> resolve(C target, C source) {
        return area.resolve(target, source);
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
