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

package fr.quatrevieux.araknemu.network.out;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Display a message box
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Aks.as#L619
 */
public final class ServerMessage {
    /**
     * Message should be directly displayed, or on connection closed ?
     *
     * Set to false for display message on connection closed
     */
    private final boolean displayNow;

    /**
     * The message ID
     *
     * See lang.swf SRV_MSG_[message id]
     */
    private final int messageId;

    /**
     * Parameters for the message
     */
    private final Object[] parameters;

    /**
     * The message box name
     * Can be null
     */
    private final @Nullable String name;

    public ServerMessage(boolean displayNow, int messageId, Object[] parameters, @Nullable String name) {
        this.displayNow = displayNow;
        this.messageId = messageId;
        this.parameters = parameters;
        this.name = name;
    }

    @Override
    public String toString() {
        return "M"
            + (displayNow ? "1" : "0") + messageId + "|" +
            StringUtils.join(parameters, ";") +
            (name != null ? "|" + name : "")
        ;
    }

    /**
     * The player do not have enough kamas for open its bank account
     *
     * @param cost The bank cost
     */
    public static ServerMessage notEnoughKamasForBank(long cost) {
        return new ServerMessage(true, 10, new Object[] { cost }, null);
    }

    /**
     * The session is closed because of the inactivity
     */
    public static ServerMessage inactivity() {
        return new ServerMessage(false, 1, new Object[] {}, null);
    }

    /**
     * The server has been stopped
     */
    public static ServerMessage shutdown() {
        return new ServerMessage(false, 4, new Object[] {}, null);
    }

    /**
     * The player has been kicked
     *
     * @param performer The kicker pseudo
     * @param cause The kick message
     */
    public static ServerMessage kick(String performer, String cause) {
        return new ServerMessage(false, 18, new Object[] {performer, cause}, null);
    }
}
