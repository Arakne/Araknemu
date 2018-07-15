package fr.quatrevieux.araknemu.game.spell.effect;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.data.value.SpellTemplateEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.*;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;

import java.util.*;
import java.util.function.Function;

/**
 * Handle spell effects
 */
final public class SpellEffectService {
    final private Map<EffectArea.Type, Function<EffectArea, SpellEffectArea>> areaFactories;

    public SpellEffectService() {
        this(new EnumMap<>(EffectArea.Type.class));

        areaFactories.put(EffectArea.Type.CELL, area -> CellArea.INSTANCE);
        areaFactories.put(EffectArea.Type.CIRCLE, CircleArea::new);
        areaFactories.put(EffectArea.Type.LINE, LineArea::new);
        areaFactories.put(EffectArea.Type.CROSS, CrossArea::new);
        areaFactories.put(EffectArea.Type.PERPENDICULAR_LINE, PerpendicularLineArea::new);
        areaFactories.put(EffectArea.Type.RING, RingArea::new);
        areaFactories.put(EffectArea.Type.CHECKERBOARD, CheckboardArea::new);
    }

    public SpellEffectService(Map<EffectArea.Type, Function<EffectArea, SpellEffectArea>> areaFactories) {
        this.areaFactories = areaFactories;
    }

    /**
     * Make the spell effect
     *
     * @param template The effect template
     * @param area The effect area
     * @param target The effect target filter
     */
    public SpellEffect make(SpellTemplateEffect template, EffectArea area, SpellEffectTarget target) {
        return new SpellTemplateEffectAdapter(template, area(area), target);
    }

    /**
     * Make an effect list
     *
     * @param templates List of effects
     * @param areas The effects areas
     * @param targets The effects targets
     */
    public List<SpellEffect> makeAll(List<SpellTemplateEffect> templates, List<EffectArea> areas, int[] targets) {
        List<SpellEffect> effects = new ArrayList<>(templates.size());

        for (int i = 0; i < templates.size(); ++i) {
            effects.add(make(templates.get(i), areas.get(i), targets.length > i ? new SpellEffectTarget(targets[i]) : SpellEffectTarget.DEFAULT));
        }

        return effects;
    }

    /**
     * Create the spell effect area from area data
     *
     * @param area The effect area data
     */
    public SpellEffectArea area(EffectArea area) {
        if (!areaFactories.containsKey(area.type())) {
            throw new NoSuchElementException("Effect area " + area + " not available");
        }

        return areaFactories.get(area.type()).apply(area);
    }
}
