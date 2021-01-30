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

package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

import java.util.Arrays;
import java.util.List;

/**
 * Add stats to player base stats
 */
final public class AddStats extends AbstractCommand {
    final private GamePlayer player;

    public AddStats(GamePlayer player) {
        this.player = player;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("Add stats to a player")
            .help(
                formatter -> formatter
                    .synopsis("addstats [characteristic] [value]")

                    .options(
                        "characteristic",
                        "The characteristic to add.\n" +
                        "This parameter is case insensitive.\n" +
                        "It's value must be one of those : " + Arrays.toString(Characteristic.values())
                    )
                    .options("value", "The value to add, must be an integer. Negative values are allowed, but be careful with negative vitality !!!")

                    .example("addstats vitality 150", "Add 150 vitality to current player")
                    .example("${player:John} addstats strength 50", "Add 50 strength to current John")
            )
            .requires(Permission.MANAGE_PLAYER)
        ;
    }

    @Override
    public String name() {
        return "addstats";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) throws AdminException {
        final Characteristic characteristic = Characteristic.valueOf(arguments.get(1).toUpperCase());

        player.properties().characteristics().base().add(
            characteristic,
            Integer.parseInt(arguments.get(2))
        );

        performer.success(
            "Characteristic changed for {} : {} = {}",
            player.name(),
            characteristic,
            player.properties().characteristics().base().get(characteristic)
        );
    }
}
