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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.listener.fight.spectator;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.spectator.Spectator;
import fr.quatrevieux.araknemu.game.fight.spectator.event.StartWatchFight;
import fr.quatrevieux.araknemu.network.game.out.fight.battlefield.AddZones;

/**
 * Send to new spectator all visible objects on the battlefield
 */
public final class SendBattlefieldObjectsToSpectator implements Listener<StartWatchFight> {
    private final Spectator spectator;

    public SendBattlefieldObjectsToSpectator(Spectator spectator) {
        this.spectator = spectator;
    }

    @Override
    public void on(StartWatchFight event) {
        final Fight fight = spectator.fight();

        final AddZones.Zone[] zones = fight.map().objects()
            .stream()
            .map(object -> new AddZones.Zone(object.cell().id(), object.size(), object.color()))
            .toArray(AddZones.Zone[]::new)
        ;

        if (zones.length > 0) {
            spectator.send(new AddZones(zones));
        }
    }

    @Override
    public Class<StartWatchFight> event() {
        return StartWatchFight.class;
    }
}
