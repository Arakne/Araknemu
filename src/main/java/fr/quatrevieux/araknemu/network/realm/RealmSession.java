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

package fr.quatrevieux.araknemu.network.realm;

import fr.quatrevieux.araknemu.core.network.session.AbstractDelegatedSession;
import fr.quatrevieux.araknemu.core.network.session.Session;
import fr.quatrevieux.araknemu.network.AccountSession;
import fr.quatrevieux.araknemu.realm.ConnectionKey;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationAccount;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;

/**
 * Wrap IoSession for Realm
 */
public final class RealmSession extends AbstractDelegatedSession implements AccountSession<AuthenticationAccount> {
    private final ConnectionKey key;

    private @Nullable AuthenticationAccount account;

    public RealmSession(Session session) {
        super(session);

        key = new ConnectionKey();
    }

    /**
     * Get or generate connection key
     */
    public ConnectionKey key() {
        return key;
    }

    @Override
    @EnsuresNonNull({"account()", "this.account"})
    @SuppressWarnings("contracts.postcondition")
    public void attach(AuthenticationAccount account) {
        this.account = account;
    }

    @Override
    public @Nullable AuthenticationAccount detach() {
        return account = null;
    }

    @Override
    @Pure
    public @Nullable AuthenticationAccount account() {
        return account;
    }

    @Override
    @Pure
    @EnsuresNonNullIf(expression = {"account()", "this.account"}, result = true)
    @SuppressWarnings({"contracts.postcondition", "contracts.conditional.postcondition.true.override", "contracts.conditional.postcondition"})
    public boolean isLogged() {
        return account != null;
    }

    @Override
    public String toString() {
        String str = "ip=" + channel().address().getAddress().getHostAddress();

        if (account != null) {
            str += "; account=" + account.id();
        }

        return str;
    }
}
