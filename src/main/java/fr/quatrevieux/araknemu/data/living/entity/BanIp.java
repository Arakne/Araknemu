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

package fr.quatrevieux.araknemu.data.living.entity;

import inet.ipaddr.IPAddressString;

import java.time.Instant;
import java.util.Optional;

/**
 * A banned IP address with mask
 */
public final class BanIp {
    private final int id;
    private final IPAddressString ipAddress;
    private final Instant updatedAt;
    private final Instant expiresAt;
    private final String cause;
    private final int banisherId;

    public BanIp(IPAddressString ipAddress, Instant updatedAt, Instant expiresAt, String cause, int banisherId) {
        this(-1, ipAddress, updatedAt, expiresAt, cause, banisherId);
    }

    public BanIp(int id, IPAddressString ipAddress, Instant updatedAt, Instant expiresAt, String cause, int banisherId) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.updatedAt = updatedAt;
        this.expiresAt = expiresAt;
        this.cause = cause;
        this.banisherId = banisherId;
    }

    /**
     * Get the BanIp entry id
     * This is the primary key
     */
    public int id() {
        return id;
    }

    /**
     * The Ip address with mask to ban
     * This value is stored as string with maximum length of 64 chars (45 chars for IPv6 + submask)
     *
     * Supports IPv4 and IPv6
     */
    public IPAddressString ipAddress() {
        return ipAddress;
    }

    /**
     * Get the creation date time
     */
    public Instant updatedAt() {
        return updatedAt;
    }

    /**
     * The expiration date
     * If not provided, the ip will be ban forever
     */
    public Optional<Instant> expiresAt() {
        return Optional.ofNullable(expiresAt);
    }

    /**
     * Get the ban cause
     * This is a human readable string
     */
    public String cause() {
        return cause;
    }

    /**
     * The banisher account id
     */
    public int banisherId() {
        return banisherId;
    }

    /**
     * Check if the ban entry is active
     */
    public boolean active() {
        return expiresAt == null || Instant.now().isBefore(expiresAt);
    }

    /**
     * Return a new instance with the banip id
     */
    public BanIp withId(int newId) {
        return new BanIp(newId, ipAddress, updatedAt, expiresAt, cause, banisherId);
    }
}
