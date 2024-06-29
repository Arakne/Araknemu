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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.simulation.effect;

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

/**
 * Simulator for damage depending on the life of the caster effect
 * Unlike {@link FixedDamageSimulator}, this effect is related to an element, so it can be reduced by armor
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.PercentLifeDamageHandler The simulated effect
 */
public final class PercentLifeDamageSimulator extends AbstractElementalDamageSimulator {
    public PercentLifeDamageSimulator(Simulator simulator, Element element) {
        super(simulator, element);
    }

    @Override
    protected Interval computeBaseDamage(FighterData caster, SpellEffect effect) {
        final int currentLife = caster.life().current();

        return Interval.of(effect.min(), Math.max(effect.max(), effect.min()))
            .map(value -> value * currentLife / 100)
        ;
    }
}
