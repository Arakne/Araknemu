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

package fr.quatrevieux.araknemu.game.listener.fight.turn;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnStopped;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FinishTurn;

/**
 * Send that a fighter turn is terminated
 */
public final class SendFightTurnStopped implements Listener<TurnStopped> {
    private final Fight fight;

    public SendFightTurnStopped(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void on(TurnStopped event) {
        fight.send(new FinishTurn(event.turn()));
    }

    @Override
    public Class<TurnStopped> event() {
        return TurnStopped.class;
    }
}
