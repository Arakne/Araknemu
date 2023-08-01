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

package fr.quatrevieux.araknemu.game.fight.ai.factory.type;

import fr.quatrevieux.araknemu.game.fight.ai.action.builder.GeneratorBuilder;
import fr.quatrevieux.araknemu.game.fight.ai.factory.AbstractAiBuilderFactory;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

/**
 * Creates the aggressive AI
 * This is the default AI
 *
 * This AI will charge the enemy if not yet on adjacent cell, and attack if possible
 */
public final class Aggressive extends AbstractAiBuilderFactory {
    private final Simulator simulator;

    public Aggressive(Simulator simulator) {
        this.simulator = simulator;
    }

    @Override
    public void configure(GeneratorBuilder builder, PlayableFighter fighter) {
        builder
            .boostSelf(simulator)
            .attack(simulator)
        ;

        // Optimisation: do not execute "move to attack" is the fighter has only close combat spell
        // because move near enemy will perform the correct movement action
        if (hasDistanceSpell(fighter)) {
            builder.moveToAttack(simulator);
        }

        builder
            .moveOrTeleportNearEnemy()
            .boostAllies(simulator)
            .heal(simulator)
        ;
    }

    /**
     * Check if the fighter has at a distance spell (i.e. a spell which can be cast from a non-adjacent cell)
     */
    private boolean hasDistanceSpell(Fighter fighter) {
        for (Spell spell : fighter.spells()) {
            final int maxRange = spell.constraints().range().max();

            // The spell range is at least of 2
            if (maxRange > 1) {
                return true;
            }

            if (spell.effects().isEmpty()) {
                continue;
            }

            final SpellEffect effect = spell.effects().get(0);

            // Has zone effect. If the max range is 0, the effect area must have at least a size of 2
            if (effect.area().size() > 2 - maxRange) {
                return true;
            }
        }

        return false;
    }
}
