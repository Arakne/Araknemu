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
import fr.quatrevieux.araknemu.data.living.entity.account.Banishment;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Instant;
import java.util.Optional;

/**
 * Represents a banishment entry
 *
 * @param <A> The account type
 */
public final class BanEntry<A extends LivingAccount> {
    private final A account;
    private final Banishment entity;
    private final @Nullable A banisher;

    public BanEntry(A account, Banishment entity) {
        this(account, entity, null);
    }

    public BanEntry(A account, Banishment entity, @Nullable A banisher) {
        this.account = account;
        this.entity = entity;
        this.banisher = banisher;
    }

    /**
     * The banned account
     */
    public A account() {
        return account;
    }

    /**
     * The banisher account
     * Can be an empty optional if the banishment is automatic
     */
    public Optional<A> banisher() {
        return Optional.ofNullable(banisher);
    }

    /**
     * Start date of the banishment
     */
    public Instant start() {
        return entity.startDate();
    }

    /**
     * End date of the banishment
     */
    public Instant end() {
        return entity.endDate();
    }

    /**
     * Ban message
     */
    public String cause() {
        return entity.cause();
    }

    /**
     * Check if the current banishment is active (i.e. the linked account is banned)
     *
     * @return true if active
     */
    public boolean active() {
        final Instant now = Instant.now();

        return now.compareTo(start()) >= 0 && now.compareTo(end()) <= 0;
    }
}
