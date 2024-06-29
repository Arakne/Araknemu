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
import fr.quatrevieux.araknemu.game.world.creature.Life;
import fr.quatrevieux.araknemu.util.Asserter;

/**
 * Simulator for damage depending on the life of the caster effect
 * This effect is related to an element, so it can be reduced by armor
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.PercentLifeLostDamageHandler The simulated effect
 * @see PercentLifeDamageSimulator The opposite effect
 */
public final class PercentLifeLostDamageSimulator extends AbstractElementalDamageSimulator {
    public PercentLifeLostDamageSimulator(Simulator simulator, Element element) {
        super(simulator, element);
    }

    @Override
    protected Interval computeBaseDamage(FighterData caster, SpellEffect effect) {
        final Life casterLife = caster.life();
        final int lostLife = Asserter.castNonNegative(casterLife.max() - casterLife.current());

        return Interval.of(effect.min(), Math.max(effect.max(), effect.min()))
            .map(value -> value * lostLife / 100)
        ;
    }
}
