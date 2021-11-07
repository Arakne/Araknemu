/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.spell.effect;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.data.value.SpellTemplateEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.CheckboardArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.CircleArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.CrossArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.LineArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.PerpendicularLineArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.RingArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.SpellEffectArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * Handle spell effects
 */
public final class SpellEffectService {
    private final Map<EffectArea.Type, Function<EffectArea, SpellEffectArea>> areaFactories;

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
        final List<SpellEffect> effects = new ArrayList<>(templates.size());

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
