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

package fr.quatrevieux.araknemu.game.admin.account;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;

/**
 * Command for revoke all temporary permissions
 */
public final class Revoke extends AbstractCommand<Void> {
    private final GameAccount account;

    public Revoke(GameAccount account) {
        this.account = account;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .help(help -> help
                .description("Revoke all temporary permissions of an account")

                .example("#Bob revoke", "Revoke permissions of Bob account")
                .example("@John revoke", "Revoke permissions of the player John")

                .seeAlso("grant", "For grant temporary permissions")
            )
            .requires(Permission.SUPER_ADMIN)
        ;
    }

    @Override
    public String name() {
        return "revoke";
    }

    @Override
    public void execute(AdminPerformer performer, Void arguments) throws AdminException {
        if (!account.isLogged()) {
            performer.error("Cannot revoke permissions for {} : the account is not logged", account.pseudo());
            return;
        }

        account.revoke(performer.account().orElse(null));

        performer.success("Permissions revoked for {}", account.pseudo());
    }
}
