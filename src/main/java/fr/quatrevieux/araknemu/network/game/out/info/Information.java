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
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.LocalDateTime;

/**
 * Information messages
 */
public final class Information extends AbstractInformationMessage {
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
    public static Information chatFlood(int remainingSeconds) {
        return new Information(115, remainingSeconds);
    }

    /**
     * An item cannot be posted to the channel
     */
    public static Information cannotPostItemOnChannel() {
        return new Information(114);
    }

    /**
     * Add life points message
     *
     * @param value The recovered life points
     */
    public static Information heal(int value) {
        return new Information(1, value);
    }

    /**
     * Message for spell learned
     *
     * @param spellId The learned spell id
     */
    public static Information spellLearn(int spellId) {
        return new Information(3, spellId);
    }

    /**
     * Send message for characteristic boost
     *
     * @param characteristic The boosted characteristic
     * @param value The boost value
     */
    public static @Nullable Information characteristicBoosted(Characteristic characteristic, int value) {
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
    public static Information positionSaved() {
        return new Information(6);
    }

    /**
     * The bank tax has been payed
     *
     * @param cost The kamas given by the player
     */
    public static Information bankTaxPayed(long cost) {
        return new Information(20, cost);
    }

    /**
     * Show the last login date and IP address
     *
     * @param localDateTime Last login date in local time
     * @param ipAddress Last login IP address
     */
    public static Information lastLogin(LocalDateTime localDateTime, String ipAddress) {
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
    public static Information currentIpAddress(String ipAddress) {
        return new Information(153, ipAddress);
    }

    /**
     * Inform fighter that a spectator has joined the fight
     *
     * @param spectatorName The spectator name
     */
    public static Information spectatorHasJoinFight(String spectatorName) {
        return new Information(36, spectatorName);
    }

    /**
     * The fight team request help
     */
    public static Information helpRequested() {
        return new Information(103);
    }

    /**
     * The help requested has been stopped
     */
    public static Information helpRequestStopped() {
        return new Information(104);
    }

    /**
     * The fight team has been locked
     */
    public static Information joinTeamLocked() {
        return new Information(95);
    }

    /**
     * The fight team has released join of new fighters
     */
    public static Information joinTeamReleased() {
        return new Information(96);
    }

    /**
     * Spectators has been blocked on the fight
     */
    public static Information spectatorsBlocked() {
        return new Information(40);
    }

    /**
     * Spectators has been allowed on the fight
     */
    public static Information spectatorsAllowed() {
        return new Information(39);
    }
}
