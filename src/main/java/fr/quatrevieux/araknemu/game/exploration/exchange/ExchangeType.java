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

package fr.quatrevieux.araknemu.game.exploration.exchange;

/**
 * Enum of all available exchange types
 *
 * See : https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L332
 */
public enum ExchangeType {
    NPC_STORE, // dialog, prepare
    PLAYER_EXCHANGE, // start
    NPC_EXCHANGE, // dialog
    FACTORY_TABLE, // start?
    PLAYER_STORE_BUY, // dialog, prepare
    BANK, // start?, dialog?, prepare
    PLAYER_STORE_SELL, // dialog, prepare
    UNKNOWN_7,
    COLLECTOR, // dialog, prepare
    UNKNOWN_9,
    HDV_SELL,
    HDV_BUY,
    UNKNOWN_12,
    UNKNOWN_13,
    UNKNOWN_14,
    MOUNT,
    MOUNT_PARK,
}
