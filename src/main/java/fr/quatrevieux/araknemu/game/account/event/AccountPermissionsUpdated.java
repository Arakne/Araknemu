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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.account.event;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;

/**
 * The admin permissions of an account has been updated
 *
 * @see GameAccount#grant(Permission...)
 * @see GameAccount#revoke(GameAccount)
 */
public final class AccountPermissionsUpdated {
    private final @Nullable GameAccount performer;
    private final GameAccount account;

    /**
     * @param performer The admin account which grants or revokes permissions. This parameter can be null
     * @param account The target account
     */
    public AccountPermissionsUpdated(@Nullable GameAccount performer, GameAccount account) {
        this.performer = performer;
        this.account = account;
    }

    /**
     * @return The admin account which grants or revokes permissions
     */
    public Optional<GameAccount> performer() {
        return Optional.ofNullable(performer);
    }

    /**
     * @return The target account
     */
    public GameAccount account() {
        return account;
    }

    /**
     * Does the updated account has access to admin console ?
     */
    public boolean authorized() {
        return account.isMaster();
    }
}
