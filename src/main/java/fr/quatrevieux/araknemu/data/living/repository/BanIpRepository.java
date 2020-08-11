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

package fr.quatrevieux.araknemu.data.living.repository;

import fr.quatrevieux.araknemu.core.dbal.repository.MutableRepository;
import fr.quatrevieux.araknemu.data.living.entity.BanIp;
import inet.ipaddr.IPAddressString;

import java.time.Instant;
import java.util.Collection;

/**
 * Repository for {@link BanIp}
 */
public interface BanIpRepository extends MutableRepository<BanIp> {
    /**
     * Get all available (i.e. not expired) banned IPs
     */
    public Collection<BanIp> available();

    /**
     * Get all updates entries after the given date
     *
     * @return The updated BanIp entities
     */
    public Collection<BanIp> updated(Instant after);

    /**
     * Disable ban entries for the given IP address
     *
     * @param ipAddress IP address to disable
     */
    public void disable(IPAddressString ipAddress);
}
