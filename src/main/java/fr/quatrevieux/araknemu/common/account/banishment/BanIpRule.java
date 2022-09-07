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

package fr.quatrevieux.araknemu.common.account.banishment;

import fr.quatrevieux.araknemu.common.account.LivingAccount;
import fr.quatrevieux.araknemu.data.living.entity.BanIp;
import inet.ipaddr.IPAddressString;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Instant;
import java.util.Optional;

/**
 * A banned ip rule
 *
 * @param <A> The banisher account type
 */
public final class BanIpRule<A extends LivingAccount> {
    private final BanIp banIp;
    private final @Nullable A banisher;

    public BanIpRule(BanIp banIp, @Nullable A banisher) {
        this.banIp = banIp;
        this.banisher = banisher;
    }

    /**
     * The create of the rule (i.e. the banisher)
     * If there is no banisher (auto ban), an empty optional is returned
     */
    public Optional<A> banisher() {
        return Optional.ofNullable(banisher);
    }

    /**
     * Get the banned ip address
     */
    public IPAddressString ipAddress() {
        return banIp.ipAddress();
    }

    /**
     * Get the ban cause / message
     */
    public String cause() {
        return banIp.cause();
    }

    /**
     * Get the ban expiration date, or empty optional for a "forever ban"
     */
    public Optional<Instant> expiresAt() {
        return banIp.expiresAt();
    }

    /**
     * Check if the ban rule is active or not
     */
    public boolean active() {
        return banIp.active();
    }
}
