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

package fr.quatrevieux.araknemu.game.player.inventory.itemset;

import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Compute special effects diff
 */
final public class EffectsDiff {
    final private List<SpecialEffect> from;
    final private List<SpecialEffect> to;

    public EffectsDiff(List<SpecialEffect> from, List<SpecialEffect> to) {
        this.from = from;
        this.to = to;
    }

    /**
     * List of effects to apply
     */
    public List<SpecialEffect> toApply() {
        if (from.isEmpty()) {
            return to;
        }

        if (to.isEmpty()) {
            return Collections.emptyList();
        }

        final List<SpecialEffect> effects = new ArrayList<>();

        for (SpecialEffect effect : to) {
            if (!from.contains(effect)) {
                effects.add(effect);
            }
        }

        return effects;
    }

    /**
     * List of effects to remove
     */
    public List<SpecialEffect> toRelieve() {
        if (from.isEmpty()) {
            return Collections.emptyList();
        }

        if (to.isEmpty()) {
            return from;
        }

        final List<SpecialEffect> effects = new ArrayList<>();

        for (SpecialEffect effect : from) {
            if (!to.contains(effect)) {
                effects.add(effect);
            }
        }

        return effects;
    }
}
