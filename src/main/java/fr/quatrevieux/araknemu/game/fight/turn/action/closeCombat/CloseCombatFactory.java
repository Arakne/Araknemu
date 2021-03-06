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

import fr.quatrevieux.araknemu.game.fight.castable.weapon.WeaponConstraintsValidator;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.FightActionFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.BaseCriticalityStrategy;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.CriticalityStrategy;

/**
 * Factory for close combat action
 */
public final class CloseCombatFactory implements FightActionFactory {
    private final FightTurn turn;
    private final WeaponConstraintsValidator validator;
    private final CriticalityStrategy criticalityStrategy;

    public CloseCombatFactory(FightTurn turn) {
        this(turn, new WeaponConstraintsValidator(), new BaseCriticalityStrategy(turn.fighter()));
    }

    public CloseCombatFactory(FightTurn turn, WeaponConstraintsValidator validator, CriticalityStrategy criticalityStrategy) {
        this.turn = turn;
        this.validator = validator;
        this.criticalityStrategy = criticalityStrategy;
    }

    @Override
    public Action create(String[] arguments) {
        return new CloseCombat(
            turn,
            turn.fighter(),
            turn.fight().map().get(Integer.parseInt(arguments[0])),
            validator,
            criticalityStrategy
        );
    }

    @Override
    public ActionType type() {
        return ActionType.CLOSE_COMBAT;
    }
}
