package fr.quatrevieux.araknemu.game.spell.effect.area;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.world.map.MapCell;

import java.util.Collections;
import java.util.Set;

/**
 * Resolve single cell
 */
final public class CellArea implements SpellEffectArea {
    final static public CellArea INSTANCE = new CellArea();

    @Override
    public <C extends MapCell> Set<C> resolve(C target, C source) {
        return Collections.singleton(target);
    }

    @Override
    public EffectArea.Type type() {
        return EffectArea.Type.CELL;
    }

    @Override
    public int size() {
        return 0;
    }
}
