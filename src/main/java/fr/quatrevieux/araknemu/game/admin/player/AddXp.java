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

package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

import java.util.List;

/**
 * Add experience to player
 */
public final class AddXp extends AbstractCommand {
    private final GamePlayer player;

    public AddXp(GamePlayer player) {
        this.player = player;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("Add experience to player")
            .help(
                formatter -> formatter
                    .synopsis("addxp [quantity]")
                    .options("quantity", "The experience quantity to add. Must be an unsigned number.")
                    .example("${player:John} addxp 1000000", "Add 1 million xp to John")
            )
            .requires(Permission.MANAGE_PLAYER)
        ;
    }

    @Override
    public String name() {
        return "addxp";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) {
        final long xp = Long.parseUnsignedLong(arguments.get(1));

        player.properties().experience().add(xp);

        performer.success("Add {} xp to {} (level = {})", xp, player.name(), player.properties().experience().level());
    }
}
