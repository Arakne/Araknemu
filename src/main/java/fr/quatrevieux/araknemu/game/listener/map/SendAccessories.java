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

package fr.quatrevieux.araknemu.game.listener.map;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.player.inventory.event.EquipmentChanged;
import fr.quatrevieux.araknemu.game.world.creature.accessory.AccessoryType;
import fr.quatrevieux.araknemu.network.game.out.object.SpriteAccessories;

/**
 * Send accessories when an equipment is changed
 */
public final class SendAccessories implements Listener<EquipmentChanged> {
    private final ExplorationPlayer player;

    public SendAccessories(ExplorationPlayer player) {
        this.player = player;
    }

    @Override
    public void on(EquipmentChanged event) {
        if (!AccessoryType.isAccessorySlot(event.slot()) && !AccessoryType.isAccessorySlot(event.entry().position())) {
            return;
        }

        player.map().send(
            new SpriteAccessories(
                player.id(),
                player.inventory().accessories()
            )
        );
    }

    @Override
    public Class<EquipmentChanged> event() {
        return EquipmentChanged.class;
    }
}
