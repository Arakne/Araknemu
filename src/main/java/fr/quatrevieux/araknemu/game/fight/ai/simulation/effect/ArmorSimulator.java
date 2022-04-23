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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.simulation.effect;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;

import java.util.EnumSet;
import java.util.Set;

public final class ArmorSimulator implements EffectSimulator {
    @Override
    public void simulate(CastSimulation simulation, CastScope.EffectScope effect) {
        int duration = effect.effect().duration();

        if (duration == 0) {
            return;
        }

        // Limit duration to 10 for compute score
        if (duration == -1 || duration > 10) {
            duration = 10;
        }

        final Set<Element> elements = effect.effect().special() == 0
            ? EnumSet.allOf(Element.class)
            : Element.fromBitSet(effect.effect().special())
        ;

        for (PassiveFighter target : effect.targets()) {
            for (Element element : elements) {
                int armor = effect.effect().min();

                // Compute boost only for caster characteristics, because we shouldn't know other fighters one
                if (target.equals(simulation.caster())) {
                    final Characteristics characteristics = simulation.caster().characteristics();
                    final int boost = 200 + characteristics.get(Characteristic.INTELLIGENCE) + characteristics.get(element.boost());

                    armor = armor * boost / 200;
                }

                simulation.addBoost(armor * duration, target);
            }
        }
    }
}
