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
import org.kohsuke.args4j.Argument;

/**
 * Add experience to player
 */
public final class AddXp extends AbstractCommand<AddXp.Arguments> {
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
    public void execute(AdminPerformer performer, Arguments arguments) {
        player.properties().experience().add(arguments.quantity());

        performer.success("Add {} xp to {} (level = {})", arguments.quantity(), player.name(), player.properties().experience().level());
    }

    @Override
    public Arguments createArguments() {
        return new Arguments();
    }

    public static final class Arguments {
        @Argument(required = true, metaVar = "quantity", usage = "The experience quantity to add. Must be an unsigned number.")
        private long quantity;

        /**
         * The experience quantity to add
         */
        public long quantity() {
            return quantity;
        }

        public void setQuantity(long quantity) {
            this.quantity = quantity;
        }
    }
}
