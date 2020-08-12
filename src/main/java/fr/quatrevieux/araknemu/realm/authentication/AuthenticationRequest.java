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

package fr.quatrevieux.araknemu.realm.authentication;

/**
 * Request authentication
 */
public interface AuthenticationRequest {
    /**
     * The username
     */
    public String username();

    /**
     * The password
     */
    public String password();

    /**
     * Called on authenticate success
     */
    public void success(AuthenticationAccount account);

    /**
     * Called on authenticate error
     */
    public void invalidCredentials();

    /**
     * Called when account is already connected
     */
    public void alreadyConnected();

    /**
     * Called when the account is logged into a game server
     */
    public void isPlaying();

    /**
     * Called when account is banned
     */
    public void banned();
}
