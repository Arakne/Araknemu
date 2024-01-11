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

import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;

/**
 * Result for critical failure for weapon cast
 */
public final class CloseCombatFailed implements ActionResult {
    private final PlayableFighter caster;
    private final Castable action;

    public CloseCombatFailed(PlayableFighter caster, Castable action) {
        this.caster = caster;
        this.action = action;
    }

    @Override
    public int action() {
        return 305;
    }

    @Override
    public PlayableFighter performer() {
        return caster;
    }

    @Override
    public Object[] arguments() {
        return new Object[] {};
    }

    @Override
    public boolean success() {
        return false;
    }

    @Override
    public boolean secret() {
        return false;
    }

    @Override
    public void apply(FightTurn turn) {
        turn.points().useActionPoints(action.apCost());
        turn.stop();
    }
}
