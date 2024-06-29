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

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.SpellScore;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;

/**
 * Simulate simple damage effect
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.DamageHandler
 */
public final class DamageSimulator extends AbstractElementalDamageSimulator {
    private final Element element;

    public DamageSimulator(Simulator simulator, Element element) {
        super(simulator, element);

        this.element = element;
    }

    @Override
    protected Interval computeBaseDamage(FighterData caster, SpellEffect effect) {
        final int boost = caster.characteristics().get(element.boost());
        final int percent = caster.characteristics().get(Characteristic.PERCENT_DAMAGE);
        final int fixed = caster.characteristics().get(Characteristic.FIXED_DAMAGE);

        return new EffectValue(effect)
            .percent(boost)
            .percent(percent)
            .fixed(fixed)
            .interval()
        ;
    }

    @Override
    public void score(SpellScore score, SpellEffect effect, Characteristics characteristics) {
        final int min = effect.min();
        final int max = Math.max(effect.max(), min);
        final int value = (min + max) / 2;
        final int boost = Math.max(100 + characteristics.get(element.boost()), 100);

        score.addDamage(value * boost / 100);
    }
}
