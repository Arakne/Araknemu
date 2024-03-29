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
import fr.quatrevieux.araknemu.game.admin.executor.argument.handler.CustomEnumOptionHandler;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import org.kohsuke.args4j.Argument;

/**
 * Add stats to player base stats
 */
public final class AddStats extends AbstractCommand<AddStats.Arguments> {
    private final GamePlayer player;

    public AddStats(GamePlayer player) {
        this.player = player;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .help(
                formatter -> formatter
                    .description("Add stats to a player")

                    .example("addstats vitality 150", "Add 150 vitality to current player")
                    .example("@John addstats strength 50", "Add 50 strength to John")

                    .with("characteristic.enum", Characteristic.class)
            )
            .requires(Permission.MANAGE_PLAYER)
            .arguments(Arguments::new)
        ;
    }

    @Override
    public String name() {
        return "addstats";
    }

    @Override
    public void execute(AdminPerformer performer, Arguments arguments) throws AdminException {
        player.properties().characteristics().base().add(
            arguments.characteristic,
            arguments.value
        );

        performer.success(
            "Characteristic changed for {} : {} = {}",
            player.name(),
            arguments.characteristic,
            player.properties().characteristics().base().get(arguments.characteristic)
        );
    }

    @SuppressWarnings("initialization.field.uninitialized")
    public static final class Arguments {
        @Argument(
            index = 0,
            required = true,
            handler = CustomEnumOptionHandler.class,
            metaVar = "CHARACTERISTIC",
            usage = "The characteristic to add.\n" +
                "This parameter is case insensitive.\n" +
                "It's value must be one of those : {{characteristic.enum}}"
        )
        private Characteristic characteristic;

        @Argument(
            index = 1,
            required = true,
            metaVar = "VALUE",
            usage = "The value to add, must be an integer. Negative values are allowed, but be careful with negative vitality !!!"
        )
        private int value;
    }
}
