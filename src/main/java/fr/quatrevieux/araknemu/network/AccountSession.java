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

package fr.quatrevieux.araknemu.network;

import fr.quatrevieux.araknemu.common.account.AbstractLivingAccount;
import fr.quatrevieux.araknemu.core.network.session.Session;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;

/**
 * Base session type attached with an account
 *
 * @param <A> The account type
 */
public interface AccountSession<A extends @NonNull AbstractLivingAccount<?>> extends Session {
    /**
     * Attach a game account to the session
     *
     * @param account Account to attach
     */
    @EnsuresNonNull("account()")
    public void attach(A account);

    /**
     * Get the attached account
     * Can be null is not yet logged
     */
    @Pure
    public @Nullable A account();

    /**
     * Check if an account is attached
     */
    @Pure
    @EnsuresNonNullIf(expression = "account()", result = true)
    public boolean isLogged();

    /**
     * Remove the attached account
     *
     * @return The attached account
     */
    public @Nullable A detach();
}
