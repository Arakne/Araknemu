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

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.FighterAI;
import fr.quatrevieux.araknemu.game.fight.ai.action.ActionGenerator;
import fr.quatrevieux.araknemu.game.fight.ai.action.Attack;
import fr.quatrevieux.araknemu.game.fight.ai.action.Boost;
import fr.quatrevieux.araknemu.game.fight.ai.action.MoveFarEnemies;
import fr.quatrevieux.araknemu.game.fight.ai.action.MoveToAttack;
import fr.quatrevieux.araknemu.game.fight.ai.factory.AiFactory;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

import java.util.Optional;

/**
 * AI for run away monsters (like Tofu)
 *
 * This AI use the smallest MP quantity for attack, and flees farthest from enemies
 */
public final class Runaway implements AiFactory {
    private final Simulator simulator;

    public Runaway(Simulator simulator) {
        this.simulator = simulator;
    }

    @Override
    public Optional<AI> create(Fighter fighter) {
        return Optional.of(
            new FighterAI(fighter, fighter.fight(), new ActionGenerator[] {
                new Attack(simulator),
                new MoveToAttack(simulator),
                new MoveFarEnemies(),
                new Boost(simulator),
            })
        );
    }
}
