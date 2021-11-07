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

package fr.quatrevieux.araknemu.common.account.banishment.event;

import fr.quatrevieux.araknemu.common.account.LivingAccount;
import fr.quatrevieux.araknemu.common.account.banishment.BanEntry;

import java.util.Optional;

/**
 * Event rasied when an account has been banned
 */
public final class AccountBanned<A extends LivingAccount> {
    private final BanEntry<A> entry;

    public AccountBanned(BanEntry<A> entry) {
        this.entry = entry;
    }

    /**
     * The ban entry
     */
    public BanEntry<A> entry() {
        return entry;
    }

    /**
     * The banned account
     */
    public A account() {
        return entry.account();
    }

    /**
     * The banisher account
     * Can be an empty optional if the banishment is automatic
     */
    public Optional<A> banisher() {
        return entry.banisher();
    }
}
