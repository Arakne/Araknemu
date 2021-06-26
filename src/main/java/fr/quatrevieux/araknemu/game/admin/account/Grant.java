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
import fr.quatrevieux.araknemu.game.admin.executor.argument.handler.CustomEnumOptionHandler;
import org.kohsuke.args4j.Argument;

/**
 * Command for grant new permissions to an account
 */
public final class Grant extends AbstractCommand<Grant.Arguments> {
    private final GameAccount account;

    public Grant(GameAccount account) {
        this.account = account;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .help(help -> help
                .description("Grant a temporary permission to an account")

                .example("#Bob grant access debug", "Grant DEBUG permission to Bob account")
                .example("@John grant access debug", "Grant DEBUG permission to the player John")

                .seeAlso("revoke", "For revoke temporary permissions")

                .with("permission.enum", Permission.class)
            )
            .requires(Permission.SUPER_ADMIN)
            .arguments(Arguments::new)
        ;
    }

    @Override
    public String name() {
        return "grant";
    }

    @Override
    public void execute(AdminPerformer performer, Arguments arguments) throws AdminException {
        // @todo Allow when --save option will be available
        if (!account.isLogged()) {
            performer.error("Cannot change permissions for {} : the account is not logged", account.pseudo());
            return;
        }

        account.grant(arguments.permissions, performer.account().orElse(null));

        performer.success("Permissions updated for {}", account.pseudo());
    }

    public static final class Arguments {
        @Argument(
            metaVar = "PERMISSIONS", required = true, handler = CustomEnumOptionHandler.class,
            usage = "List of permissions to grant on the current account." +
                "\nAvailable permissions : {{permission.enum}}"
        )
        private Permission[] permissions;
    }
}
