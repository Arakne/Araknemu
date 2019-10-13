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

import fr.quatrevieux.araknemu.core.network.Session;

/**
 * Living account, connected to a session
 */
public interface LivingAccount<S extends Session> {
    /**
     * Check if the account is a master (admin) account
     */
    public boolean isMaster();

    /**
     * Get the account id
     */
    public int id();

    /**
     * Get the account pseudo
     */
    public String pseudo();

    /**
     * Get the community ID
     *
     * @todo constant, enum ?
     */
    public int community();

    /**
     * Attach the account to the session
     */
    public void attach(S session);

    /**
     * Detach the account from the session
     */
    public void detach();

    /**
     * Check if the account is logged
     */
    public boolean isLogged();
}
