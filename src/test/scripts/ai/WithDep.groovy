import fr.quatrevieux.araknemu.game.fight.ai.action.builder.GeneratorBuilder
import fr.quatrevieux.araknemu.game.fight.ai.factory.AbstractAiBuilderFactory
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter

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

class WithDep extends AbstractAiBuilderFactory {
    public Simulator simulator

    WithDep(Simulator simulator) {
        this.simulator = simulator
    }

    @Override
    protected void configure(GeneratorBuilder builder) {
        // Do nothing
    }
}
