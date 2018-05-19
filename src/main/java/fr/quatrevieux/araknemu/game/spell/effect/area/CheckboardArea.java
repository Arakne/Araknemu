package fr.quatrevieux.araknemu.game.spell.effect.area;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.world.map.MapCell;

import java.util.Set;

/**
 * Resolve ring area (circle border)
 */
final public class CheckboardArea implements SpellEffectArea {
    final private CircularArea area;

    public CheckboardArea(EffectArea area) {
        final int mod = area.size() % 2;

        this.area = new CircularArea(area, distance -> distance <= area.size() && distance % 2 == mod);
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
