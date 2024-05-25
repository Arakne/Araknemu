import fr.quatrevieux.araknemu.game.fight.ai.factory.scripting.AbstractScriptingAiBuilderFactory
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator

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


class Condition extends AbstractScriptingAiBuilderFactory {
    Simulator simulator

    Condition(Simulator simulator) {
        this.simulator = simulator
    }

    @Override
    protected void configure() {
        whether({ fighter().life().current() > 100 }) {
            attackFromBestCell(simulator)
            moveNearEnemy()

            otherwise {
                heal(simulator)
                attackFromNearestCell(simulator)
                moveFarEnemies()
            }
        }
    }
}
