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

package fr.quatrevieux.araknemu.game.fight.turn.action.closeCombat;

import fr.quatrevieux.araknemu.game.fight.castable.closeCombat.CloseCombatValidator;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.FightActionFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.CriticalityStrategy;
import fr.quatrevieux.araknemu.util.ParseUtils;

/**
 * Factory for close combat action
 */
public final class CloseCombatFactory implements FightActionFactory {
    private final CloseCombatValidator validator;
    private final CriticalityStrategy criticalityStrategy;

    public CloseCombatFactory(CloseCombatValidator validator, CriticalityStrategy criticalityStrategy) {
        this.validator = validator;
        this.criticalityStrategy = criticalityStrategy;
    }

    @Override
    public CloseCombat create(PlayableFighter fighter, String[] arguments) {
        if (arguments.length < 1) {
            throw new IllegalArgumentException("Invalid close combat arguments");
        }

        final FightMap map = fighter.fight().map();
        final int cellId = ParseUtils.parseNonNegativeInt(arguments[0]);

        if (cellId >= map.size()) {
            throw new IllegalArgumentException("Invalid target cell");
        }

        return new CloseCombat(
            fighter,
            map.get(cellId),
            validator,
            criticalityStrategy
        );
    }

    @Override
    public ActionType type() {
        return ActionType.CLOSE_COMBAT;
    }
}
