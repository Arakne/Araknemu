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

package fr.quatrevieux.araknemu.game.listener.fight.spectator;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.spectator.Spectator;
import fr.quatrevieux.araknemu.game.fight.spectator.event.StopWatchFight;
import fr.quatrevieux.araknemu.network.game.out.fight.CancelFight;

/**
 * Send leave packet for the spectator when leave the fight
 */
public final class SendSpectatorLeaveFight implements Listener<StopWatchFight> {
    private final Spectator spectator;

    public SendSpectatorLeaveFight(Spectator spectator) {
        this.spectator = spectator;
    }

    @Override
    public void on(StopWatchFight event) {
        spectator.send(new CancelFight());
    }

    @Override
    public Class<StopWatchFight> event() {
        return StopWatchFight.class;
    }
}
