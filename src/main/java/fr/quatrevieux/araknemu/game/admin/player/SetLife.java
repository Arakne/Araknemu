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
import fr.quatrevieux.araknemu.util.ParseUtils;
import org.checkerframework.checker.index.qual.NonNegative;
import org.kohsuke.args4j.Argument;

/**
 * Change the player life
 */
public final class SetLife extends AbstractCommand<SetLife.Arguments> {
    private final GamePlayer player;

    public SetLife(GamePlayer player) {
        this.player = player;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .help(
                formatter -> formatter
                    .description("Change the player current life")
                    .synopsis("setlife number|max")
                    .example("setlife 300", "Set the current player life to 300")
                    .example("setlife max", "Set full life to the current player")
                    .example("@John setlife 250", "Set John's life to 250")
            )
            .requires(Permission.MANAGE_PLAYER)
            .arguments(Arguments::new)
        ;
    }

    @Override
    public String name() {
        return "setlife";
    }

    @Override
    public void execute(AdminPerformer performer, Arguments arguments) throws AdminException {
        if (arguments.max()) {
            player.properties().life().set(player.properties().life().max());

            performer.success("{} retrieve all his life", player.name());
        } else {
            player.properties().life().set(arguments.number());

            performer.success("Life of {} is set to {}", player.name(), arguments.value);
        }
    }

    @SuppressWarnings("initialization.field.uninitialized")
    public static final class Arguments {
        @Argument(required = true, metaVar = "number|max")
        private String value;

        public String value() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean max() {
            return "max".equalsIgnoreCase(value);
        }

        public @NonNegative int number() {
            return ParseUtils.parseNonNegativeInt(value);
        }
    }
}
