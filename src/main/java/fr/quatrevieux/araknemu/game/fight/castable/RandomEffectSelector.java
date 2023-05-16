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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable;

import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.util.Asserter;

import java.util.ArrayList;
import java.util.List;

/**
 * Perform randomized effects selection
 */
public final class RandomEffectSelector {
    private static final RandomUtil RANDOM = RandomUtil.createShared();

    /**
     * Utility class : disable constructor
     */
    private RandomEffectSelector() {}

    /**
     * Select a randomized effect, if exists
     *
     * If there is no randomized effects, this method will simply return the parameter
     * Only one probable effect will be chosen, depending on {@link SpellEffect#probability()}
     */
    public static List<SpellEffect> select(List<SpellEffect> effects) {
        final List<SpellEffect> selectedEffects = new ArrayList<>(effects.size());
        final List<SpellEffect> probableEffects = new ArrayList<>();

        int probabilitySum = 0;

        for (SpellEffect effect : effects) {
            final int probability = effect.probability();

            if (probability == 0) {
                selectedEffects.add(effect);
            }  else {
                probableEffects.add(effect);
                probabilitySum += probability;
            }
        }

        // No probable effects
        if (probableEffects.isEmpty()) {
            return effects;
        }

        if (probableEffects.size() == 1) {
            probabilitySum = 100;
        }

        int dice = RANDOM.nextInt(Asserter.assertPositive(probabilitySum));

        for (SpellEffect effect : probableEffects) {
            dice -= effect.probability();

            if (dice <= 0) {
                selectedEffects.add(effect);
                break;
            }
        }

        return selectedEffects;
    }
}
