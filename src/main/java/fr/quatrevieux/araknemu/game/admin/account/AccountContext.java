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

package fr.quatrevieux.araknemu.game.admin.account;

import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.context.AbstractContext;
import fr.quatrevieux.araknemu.game.admin.context.Context;
import fr.quatrevieux.araknemu.game.admin.context.AbstractContextConfigurator;
import fr.quatrevieux.araknemu.game.admin.context.SimpleContext;

import java.util.List;

/**
 * Context for account
 */
public final class AccountContext extends AbstractContext<AccountContext> {
    private final Context globalContext;
    private final GameAccount account;

    public AccountContext(Context globalContext, GameAccount account, List<AbstractContextConfigurator<AccountContext>> configurators) {
        super(configurators);

        this.globalContext = globalContext;
        this.account = account;
    }

    @Override
    protected SimpleContext createContext() {
        return new SimpleContext(globalContext);
    }

    /**
     * Get the related account
     */
    public GameAccount account() {
        return account;
    }
}
