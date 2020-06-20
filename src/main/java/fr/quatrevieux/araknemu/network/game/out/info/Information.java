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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.network.game.out.info;

import fr.quatrevieux.araknemu.data.constant.Characteristic;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Information messages
 */
final public class Information extends InformationMessage {
    public Information(Entry... entries) {
        super(Type.INFO, entries);
    }

    public Information(int id) {
        this(new Entry(id));
    }

    public Information(int id, Object... arguments) {
        this(new Entry(id, arguments));
    }

    /**
     * Message for global channel flood
     *
     * @param remainingSeconds Remaining time in seconds before send another message
     */
    static public Information chatFlood(int remainingSeconds) {
        return new Information(115, remainingSeconds);
    }

    /**
     * An item cannot be posted to the channel
     */
    static public Information cannotPostItemOnChannel() {
        return new Information(114);
    }

    /**
     * Add life points message
     *
     * @param value The recovered life points
     */
    static public Information heal(int value) {
        return new Information(1, value);
    }

    /**
     * Message for spell learned
     *
     * @param spellId The learned spell id
     */
    static public Information spellLearn(int spellId) {
        return new Information(3, spellId);
    }

    /**
     * Send message for characteristic boost
     *
     * @param characteristic The boosted characteristic
     * @param value The boost value
     */
    static public Information characteristicBoosted(Characteristic characteristic, int value) {
        switch (characteristic) {
            case WISDOM:
                return new Information(9, value);
            case STRENGTH:
                return new Information(10, value);
            case LUCK:
                return new Information(11, value);
            case AGILITY:
                return new Information(12, value);
            case VITALITY:
                return new Information(13, value);
            case INTELLIGENCE:
                return new Information(14, value);
        }

        return null;
    }

    /**
     * The position of the player is saved
     */
    static public Information positionSaved() {
        return new Information(6);
    }

    /**
     * The bank tax has been payed
     *
     * @param cost The kamas given by the player
     */
    static public Information bankTaxPayed(long cost) {
        return new Information(20, cost);
    }

    /**
     * Show the last login date and IP address
     *
     * @param date Last login date
     * @param ipAddress Last login IP address
     */
    static public Information lastLogin(Instant date, String ipAddress) {
        final LocalDateTime localDateTime = LocalDateTime.ofInstant(date, ZoneId.systemDefault());

        return new Information(
            152,
            localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth(),
            localDateTime.getHour(), localDateTime.getMinute(),
            ipAddress
        );
    }

    /**
     * Show the current IP address
     *
     * @param ipAddress IP address to show
     */
    static public Information currentIpAddress(String ipAddress) {
        return new Information(153, ipAddress);
    }
}
