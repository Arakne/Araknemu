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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.simulation.effect;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.SpellScore;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;

/**
 * Handle simulator for reflect damage effect
 *
 * For simulation, will be considered as simple characteristic boost to simplify the code.
 * Also provides a buff effect simulator for compute reflected damage.
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.armor.ReflectDamageHandler The actual effect handler
 */
public final class ReflectDamageSimulator implements EffectSimulator, BuffEffectSimulator {
    private final EffectSimulator baseSimulator;

    /**
     * @param multiplier The value multiplier for buff score. Forwards to {@link AlterCharacteristicSimulator}.
     */
    public ReflectDamageSimulator(int multiplier) {
        this.baseSimulator = new AlterCharacteristicSimulator(multiplier);
    }

    @Override
    public void simulate(CastSimulation simulation, AI ai, CastScope.EffectScope<? extends FighterData, ? extends BattlefieldCell> effect) {
        baseSimulator.simulate(simulation, ai, effect);
    }

    @Override
    public void score(SpellScore score, SpellEffect effect, Characteristics characteristics) {
        baseSimulator.score(score, effect, characteristics);
    }

    @Override
    public Damage onReduceableDamage(CastSimulation simulation, Buff buff, FighterData target, Damage damage) {
        final int wisdom = buff.target().characteristics().get(Characteristic.WISDOM);
        final int baseReflect = buff.effect().min();
        final int reflect = baseReflect + baseReflect * wisdom / 100;

        if (reflect > 0) {
            damage.reflect(reflect);
        }

        return damage;
    }
}
