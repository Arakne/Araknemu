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
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldObject;
import fr.quatrevieux.araknemu.game.fight.spectator.Spectator;
import fr.quatrevieux.araknemu.game.fight.spectator.event.StartWatchFight;
import fr.quatrevieux.araknemu.network.game.out.fight.battlefield.AddZones;
import fr.quatrevieux.araknemu.network.game.out.game.UpdateCells;

import java.util.ArrayList;
import java.util.List;

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

        final List<AddZones.Zone> zones = new ArrayList<>();
        final List<UpdateCells.Data> cells = new ArrayList<>();

        for (BattlefieldObject object : fight.map().objects()) {
            if (!object.visible()) {
                continue;
            }

            zones.add(new AddZones.Zone(object.cell().id(), object.size(), object.color()));

            final UpdateCells.PropertyValue<?>[] properties = object.cellsProperties();

            if (properties.length > 0) {
                cells.add(UpdateCells.Data.fromProperties(object.cell().id(), true, properties));
            }
        }

        if (!zones.isEmpty()) {
            spectator.send(new AddZones(zones.toArray(new AddZones.Zone[0])));
        }

        if (!cells.isEmpty()) {
            spectator.send(new UpdateCells(cells.toArray(new UpdateCells.Data[0])));
        }
    }

    @Override
    public Class<StartWatchFight> event() {
        return StartWatchFight.class;
    }
}
