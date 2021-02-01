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

package fr.quatrevieux.araknemu.network.game.out.fight.action;

import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import org.apache.commons.lang3.StringUtils;

/**
 * Send the fight action
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/GameActions.as#L127
 */
public final class FightAction {
    private final ActionResult result;

    public FightAction(ActionResult result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return
            "GA" + (result.success() ? "0" : "") +  ";" +
            result.action() + ";" +
            result.performer().id() + ";" +
            StringUtils.join(result.arguments(), ",")
        ;
    }
}
