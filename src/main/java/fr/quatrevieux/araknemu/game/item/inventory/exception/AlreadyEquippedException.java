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

package fr.quatrevieux.araknemu.game.item.inventory.exception;

/**
 * Raise when the item is already equipped
 * When this exception is raised, an error "ALREADY_EQUIPPED" should be sent to the client, and nothing should be done
 *
 * @see fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.EquipOnceConstraint The constraint that raise this exception
 */
public class AlreadyEquippedException extends InventoryException {
}
