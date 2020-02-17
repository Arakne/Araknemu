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
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

import java.util.List;

/**
 * Change the player life
 */
final public class SetLife extends AbstractCommand {
    final private GamePlayer player;

    public SetLife(GamePlayer player) {
        this.player = player;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("Change the player current life")
            .help(
                formatter -> formatter
                    .synopsis("setlife [number|max]")
                    .example("setlife 300", "Set the player life to 300")
                    .example("setlife max", "Set full life to the player")
                    .example("${player:John} setlife 250", "Set John's life to 250")
            )
            .requires(Permission.MANAGE_PLAYER)
        ;
    }

    @Override
    public String name() {
        return "setlife";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) throws AdminException {
        if (arguments.get(1).equalsIgnoreCase("max")) {
            player.properties().life().set(player.properties().life().max());

            performer.success("{} retrieve all his life", player.name());
        } else {
            player.properties().life().set(Integer.parseUnsignedInt(arguments.get(1)));

            performer.success("Life of {} is set to {}", player.name(), arguments.get(1));
        }
    }
}
