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
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.SpellScore;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.util.Formula;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;

import java.util.EnumSet;
import java.util.Set;

public final class ArmorSimulator implements EffectSimulator, BuffEffectSimulator {
    @Override
    public void simulate(CastSimulation simulation, AI ai, CastScope.EffectScope<? extends FighterData, ? extends BattlefieldCell> effect) {
        if (effect.effect().duration() == 0) {
            return;
        }

        final int duration = Formula.capedDuration(effect.effect().duration());

        final Set<Element> elements = effect.effect().special() == 0
            ? EnumSet.allOf(Element.class)
            : Element.fromBitSet(effect.effect().special())
        ;

        for (FighterData target : effect.targets()) {
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

    @Override
    public void score(SpellScore score, SpellEffect effect, Characteristics characteristics) {
        final int boost = Math.max(100 + characteristics.get(Characteristic.INTELLIGENCE), 100);

        score.addBoost(effect.min() * boost / 100);
    }

    @Override
    public Damage onReduceableDamage(Buff buff, FighterData target, Damage damage) {
        if (!supportsElement(buff, damage.element())) {
            return damage;
        }

        final Characteristics characteristics = target.characteristics();

        final int boost = 200 + characteristics.get(Characteristic.INTELLIGENCE) + characteristics.get(damage.element().boost());
        final int reduce = buff.effect().min() * boost / 200;

        if (reduce < 0) {
            return damage;
        }

        damage.reduce(reduce);

        return damage;
    }

    /**
     * Check if the armor supports the damage element
     *
     * @param buff The buff
     * @param element The damage element
     *
     * @return true if the armor supports the element
     */
    private boolean supportsElement(Buff buff, Element element) {
        return buff.effect().special() == 0 || Element.fromBitSet(buff.effect().special()).contains(element);
    }
}
