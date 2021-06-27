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

package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.out.ServerMessage;

import java.util.List;

/**
 * Command for kick a player
 */
public final class Kick extends AbstractCommand<List<String>> {
    private final GamePlayer player;

    public Kick(GamePlayer player) {
        this.player = player;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .help(help -> help
                .description("Kick the player of the server")
                .synopsis("@[player] kick [MESSAGE]")
                .option("MESSAGE", "Optional. The kick reason")
                .example("@John kick Last warning", "Kick John with message 'Last warning'")

                .seeAlso("ban", "For ban the player")
            )
            .requires(Permission.MANAGE_PLAYER)
        ;
    }

    @Override
    public String name() {
        return "kick";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) throws AdminException {
        checkNotSelf(performer);

        final String message = arguments.isEmpty() ? "" : "\n" + String.join(" ", arguments);

        player.account().kick(ServerMessage.kick(
            performer.account().map(GameAccount::pseudo).orElse("system"),
            message
        ));
    }

    /**
     * Check to ensure that the admin do not kick himself
     */
    private void checkNotSelf(AdminPerformer performer) throws CommandException {
        if (performer.account().filter(player.account()::equals).isPresent()) {
            throw new CommandException(name(), "Cannot kick yourself");
        }
    }
}
