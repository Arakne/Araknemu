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

package fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.object;

import fr.quatrevieux.araknemu.data.world.entity.environment.npc.ResponseAction;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.Action;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.ActionFactory;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Remove the object from inventory
 *
 * Arguments : [itemId],[quantity],[required]
 *
 * With :
 * - itemId   : The item template id to remove {@link fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate#id()}
 * - quantity : The required quantity (default value is 1)
 * - required : Does the item is needed to get the response ?
 *              If set to 1 (or not given) the quantity of the item is checked before display the response
 *              If set to 0, the response is always displayed
 */
public final class RemoveObject implements Action {
    private final int itemId;
    private final int quantity;
    private final boolean required;

    public RemoveObject(int itemId, int quantity, boolean required) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.required = required;
    }

    @Override
    public boolean check(ExplorationPlayer player) {
        if (!required) {
            return true;
        }

        if (quantity == 1) {
            return player.player().inventory()
                .stream()
                .anyMatch(entry -> entry.item().template().id() == itemId)
            ;
        }

        return player.player().inventory()
            .stream()
            .filter(entry -> entry.item().template().id() == itemId)
            .mapToInt(InventoryEntry::quantity)
            .sum() >= quantity
        ;
    }

    @Override
    public void apply(ExplorationPlayer player) {
        final Collection<InventoryEntry> entries = player.player().inventory()
            .stream()
            .filter(entry -> entry.item().template().id() == itemId)
            .collect(Collectors.toList())
        ;

        int currentQuantity = quantity;

        for (InventoryEntry entry : entries) {
            final int toRemove = Math.min(entry.quantity(), currentQuantity);

            entry.remove(toRemove);
            currentQuantity -= toRemove;

            if (currentQuantity <= 0) {
                break;
            }
        }
    }

    public static final class Factory implements ActionFactory {
        @Override
        public String type() {
            return "REM_OBJECT";
        }

        @Override
        public Action create(ResponseAction entity) {
            final String[] arguments = StringUtils.split(entity.arguments(), ",", 3);

            return new RemoveObject(
                Integer.parseInt(arguments[0]),
                arguments.length > 1 ? Integer.parseInt(arguments[1]) : 1,
                arguments.length < 3 || "1".equals(arguments[2])
            );
        }
    }
}
