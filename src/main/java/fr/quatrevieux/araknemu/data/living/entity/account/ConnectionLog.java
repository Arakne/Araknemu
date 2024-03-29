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

package fr.quatrevieux.araknemu.data.living.entity.account;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;

import java.time.Instant;

/**
 * Log the connexion and logout on the server
 */
public final class ConnectionLog {
    private final int accountId;
    private final Instant startDate;
    private final String ipAddress;

    private @Nullable Integer serverId;
    private @Nullable Integer playerId;
    private @Nullable Instant endDate;
    private @Nullable String clientUid;

    public ConnectionLog(int accountId, Instant startDate, String ipAddress, @Nullable Integer serverId, @Nullable Integer playerId, @Nullable Instant endDate, @Nullable String clientUid) {
        this.accountId = accountId;
        this.startDate = startDate;
        this.ipAddress = ipAddress;
        this.serverId = serverId;
        this.playerId = playerId;
        this.endDate = endDate;
        this.clientUid = clientUid;
    }

    public ConnectionLog(int accountId, Instant startDate, String ipAddress) {
        this(accountId, startDate, ipAddress, null, null, null, null);
    }

    /**
     * The connected account id
     * This is a part of the primary key
     *
     * @see Account#id()
     */
    @Pure
    public int accountId() {
        return accountId;
    }

    /**
     * The start date of the game session (i.e. the connection date)
     * Stored as UTC date time
     * This is a part of the primary key
     */
    @Pure
    public Instant startDate() {
        return startDate;
    }

    /**
     * The connection ip address
     * This address can be either an IPv4 or IPv6, so the value length can reach 45 characters
     * The value cannot be null
     */
    @Pure
    public String ipAddress() {
        return ipAddress;
    }

    /**
     * Get the selected server id
     * Can be null is the server is not yet selected
     */
    @Pure
    public @Nullable Integer serverId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    /**
     * Get the selected player id
     * Can be null is the player is not yet selected
     */
    @Pure
    public @Nullable Integer playerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    /**
     * Get the end session date
     * This value is null when the session is not terminated
     */
    @Pure
    public @Nullable Instant endDate() {
        return endDate;
    }

    public void setEndDate(@Nullable Instant endDate) {
        this.endDate = endDate;
    }

    /**
     * The client unique id
     * Can be null if not set
     *
     * @see fr.quatrevieux.araknemu.network.game.in.account.ClientUid
     */
    @Pure
    public @Nullable String clientUid() {
        return clientUid;
    }

    public void setClientUid(@Nullable String clientUid) {
        this.clientUid = clientUid;
    }
}
