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

package fr.quatrevieux.araknemu.common.account;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.core.network.session.Session;

/**
 * Abstract class for Living Account
 * @param <S> The session type
 */
abstract public class AbstractLivingAccount<S extends Session> implements LivingAccount<S> {
    final protected Account account;

    protected S session;

    public AbstractLivingAccount(Account account) {
        this.account = account;
    }

    @Override
    public boolean isMaster() {
        return !account.permissions().isEmpty();
    }

    @Override
    public int id() {
        return account.id();
    }

    @Override
    public String pseudo() {
        return account.pseudo();
    }

    @Override
    public int community() {
        return 0;
    }

    @Override
    public void attach(S session) {
        this.session = session;
    }

    @Override
    public void detach() {
        this.session = null;
    }

    @Override
    public boolean isLogged() {
        return session != null && session.isAlive();
    }
}
