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

package fr.quatrevieux.araknemu.game.fight.turn.action.cast;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.spell.Spell;

/**
 * Result for critical failure for spell cast
 */
public final class CastFailed implements ActionResult {
    private final Fighter caster;
    private final Spell spell;

    public CastFailed(Fighter caster, Spell spell) {
        this.caster = caster;
        this.spell = spell;
    }

    @Override
    public int action() {
        return 302;
    }

    @Override
    public Fighter performer() {
        return caster;
    }

    @Override
    public Object[] arguments() {
        return new Object[] { spell.id() };
    }

    @Override
    public boolean success() {
        return false;
    }

    @Override
    public void apply(FightTurn turn) {
        turn.points().useActionPoints(spell.apCost());

        if (spell.endsTurnOnFailure()) {
            turn.stop();
        }
    }
}
