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
import fr.quatrevieux.araknemu.common.account.banishment.BanIpRule;

/**
 * An IP address has been banned
 *
 * @param <A> The account type
 */
public final class IpBanned<A extends LivingAccount> {
    private final BanIpRule<A> rule;

    public IpBanned(BanIpRule<A> rule) {
        this.rule = rule;
    }

    /**
     * Get the ban ip rule
     */
    public BanIpRule<A> rule() {
        return rule;
    }
}
