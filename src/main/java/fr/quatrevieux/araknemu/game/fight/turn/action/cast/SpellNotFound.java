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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.turn.action.cast;

import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.SendPacket;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

import java.time.Duration;

/**
 * Fake action to perform when an invalid spell is required
 */
public final class SpellNotFound implements Action {
    private final Fighter caster;

    public SpellNotFound(Fighter caster) {
        this.caster = caster;
    }

    @Override
    public boolean validate(Turn turn) {
        caster.apply(new SendPacket(Error.cantCastNotFound()));

        return false;
    }

    @Override
    public ActionResult start() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ActiveFighter performer() {
        return caster;
    }

    @Override
    public ActionType type() {
        return ActionType.CAST;
    }

    @Override
    public Duration duration() {
        throw new UnsupportedOperationException();
    }
}
