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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.realm.authentication;

import fr.quatrevieux.araknemu.common.account.AbstractLivingAccount;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.network.realm.RealmSession;

/**
 * AuthenticationAccount entity for realm
 */
final public class AuthenticationAccount extends AbstractLivingAccount<RealmSession> {
    final private AuthenticationService service;

    public AuthenticationAccount(Account account, AuthenticationService service) {
        super(account);

        this.service = service;
    }

    /**
     * Check if the given password is valid
     *
     * @param password Input password
     */
    public boolean checkPassword(String password) {
        return account.password().equals(password);
    }

    /**
     * Attach account to a session
     */
    @Override
    public void attach(RealmSession session) {
        session.attach(this);
        service.login(this);

        super.attach(session);
    }

    /**
     * Detach account from session
     */
    @Override
    public void detach() {
        session.detach();
        service.logout(this);

        super.detach();
    }

    /**
     * Get the secret question
     */
    public String question() {
        return account.question();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthenticationAccount that = (AuthenticationAccount) o;

        return account.id() == that.account.id();
    }

    @Override
    public int hashCode() {
        return 23 * account.id();
    }
}
