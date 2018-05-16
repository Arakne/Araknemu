package fr.quatrevieux.araknemu.game.spell.effect.area;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.world.map.MapCell;

import java.util.Set;

/**
 * Resolve cells from area and target cell
 */
public interface SpellEffectArea {
    /**
     * Resolve the cells from an effect area
     *
     * @param target The target cell
     * @param source The source cell (caster cell)
     */
    public <C extends MapCell> Set<C> resolve(C target, C source);

    /**
     * The area type
     */
    public EffectArea.Type type();

    /**
     * The area size
     */
    public int size();
}
