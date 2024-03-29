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
import fr.quatrevieux.araknemu.game.player.experience.PlayerExperienceService;
import org.checkerframework.checker.index.qual.NonNegative;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 * Add experience to player
 */
public final class AddXp extends AbstractCommand<AddXp.Arguments> {
    private final GamePlayer player;
    private final PlayerExperienceService service;

    public AddXp(GamePlayer player, PlayerExperienceService service) {
        this.player = player;
        this.service = service;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .help(
                formatter -> formatter
                    .description("Add experience to player")
                    .example("@John addxp 1000000", "Add 1 million xp to John")
                    .example("@John addxp --level 150", "Add xp to John to reach level 150")
            )
            .requires(Permission.MANAGE_PLAYER)
            .arguments(Arguments::new)
        ;
    }

    @Override
    public String name() {
        return "addxp";
    }

    @Override
    public void execute(AdminPerformer performer, Arguments arguments) {
        long quantity = arguments.quantity;

        if (arguments.level > 0) {
            if (arguments.level <= player.properties().experience().level()) {
                performer.error("The player level ({}) is already higher than the target level ({})", player.properties().experience().level(), arguments.level);
                return;
            }

            quantity = Math.max(service.byLevel(arguments.level).experience() - player.properties().experience().current(), 0);
        }

        player.properties().experience().add(quantity);

        performer.success("Add {} xp to {} (level = {})", quantity, player.name(), player.properties().experience().level());
    }

    public static final class Arguments {
        @Argument(
            required = false,
            metaVar = "QUANTITY",
            usage = "The experience quantity to add. Must be an unsigned number."
        )
        private @NonNegative long quantity = 0;

        @Option(
            name = "--level",
            aliases = {"-l"},
            usage = "The target level. If set, the quantity will be calculated to reach this level. Must be a positive number."
        )
        private @NonNegative int level = 0;
    }
}
