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

package fr.quatrevieux.araknemu.game.fight.spectator;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.listener.fight.spectator.LeaveSpectatorOnDisconnect;
import fr.quatrevieux.araknemu.game.listener.fight.spectator.SendFightStateToSpectator;
import fr.quatrevieux.araknemu.game.listener.fight.spectator.SendSpectatorLeaveFight;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Default implementation of the Spectator factory which add required event listeners
 */
public final class DefaultSpectatorFactory implements SpectatorFactory {
    @Override
    public Spectator create(GamePlayer player, Fight fight) {
        final Spectator spectator = new Spectator(player, fight);

        spectator.dispatcher().add(new SendFightStateToSpectator(spectator));
        spectator.dispatcher().add(new SendSpectatorLeaveFight(spectator));
        spectator.dispatcher().add(new LeaveSpectatorOnDisconnect(spectator));

        return spectator;
    }
}
