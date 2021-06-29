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
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.Restrictions;

import java.util.ArrayList;
import java.util.List;

/**
 * Change the player restrictions
 */
public final class Restriction extends AbstractCommand<List<String>> {
    private final GamePlayer player;

    public Restriction(GamePlayer player) {
        this.player = player;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .help(
                formatter -> formatter
                    .description("Change the player restrictions")
                    .synopsis("restriction [+/-]RESTRICTION...")

                    .option("+RESTRICTION", "Active the restriction on the player")
                    .option("-RESTRICTION", "Remove the restriction on the player")

                    .line("AVAILABLE RESTRICTIONS :",
                        "\t{{restriction.enum}}",
                        "\tNote: The name is case insensitive"
                    )
                    .line("WARNING : This is a debug feature, and can cause bugs if misused")
                    .with("restriction.enum", Restrictions.Restriction.class)

                    .example("restriction +DENY_CHALLENGE", "The player will not be allowed perform challenges")
                    .example("restriction +DENY_CHAT -ALLOW_MOVE_ALL_DIRECTION", "Perform multiple changes")
                    .example("@John restriction +DENY_CHALLENGE", "Change John's restrictions")
            )
            .requires(Permission.MANAGE_PLAYER)
            .requires(Permission.DEBUG)
        ;
    }

    @Override
    public String name() {
        return "restriction";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) throws AdminException {
        final List<Restrictions.Restriction> toSet = new ArrayList<>();
        final List<Restrictions.Restriction> toUnset = new ArrayList<>();

        for (String argument : arguments) {
            final Restrictions.Restriction restriction = Restrictions.Restriction.valueOf(argument.substring(1).toUpperCase());

            switch (argument.charAt(0)) {
                case '+':
                    toSet.add(restriction);
                    break;

                case '-':
                    toUnset.add(restriction);
                    break;

                default:
                    error("Invalid or missing operation on argument " + argument);
            }
        }

        if (!toSet.isEmpty()) {
            player.restrictions().set(toSet.toArray(new Restrictions.Restriction[0]));
        }

        if (!toUnset.isEmpty()) {
            player.restrictions().unset(toUnset.toArray(new Restrictions.Restriction[0]));
        }

        performer.success("{} restrictions updated", player.name());
    }
}
