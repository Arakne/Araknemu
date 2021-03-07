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

package fr.quatrevieux.araknemu.game.listener.fight.turn.action;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.turn.action.event.FightActionTerminated;
import fr.quatrevieux.araknemu.game.world.util.Sender;
import fr.quatrevieux.araknemu.network.game.out.fight.action.FinishFightAction;

/**
 * Send to clients a terminated fight action
 */
public final class SendFightActionTerminated implements Listener<FightActionTerminated> {
    @Override
    public void on(FightActionTerminated event) {
        if (!event.action().performer().dead() && event.action().performer() instanceof Sender) {
            Sender.class.cast(event.action().performer()).send(new FinishFightAction(event.action()));
        }
    }

    @Override
    public Class<FightActionTerminated> event() {
        return FightActionTerminated.class;
    }
}
