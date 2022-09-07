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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.listener.fight;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.team.event.AllowJoinTeamChanged;
import fr.quatrevieux.araknemu.network.game.out.info.Information;

/**
 * Send to the team that block joiner option has been changed
 *
 * Note: this listener should be only available on placement state
 */
public final class SendJoinTeamOptionChangedMessage implements Listener<AllowJoinTeamChanged> {
    @Override
    public void on(AllowJoinTeamChanged event) {
        event.options().team().send(
            event.joinAllowed()
                ? Information.joinTeamReleased()
                : Information.joinTeamLocked()
        );
    }

    @Override
    public Class<AllowJoinTeamChanged> event() {
        return AllowJoinTeamChanged.class;
    }
}
