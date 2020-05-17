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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Change the player restrictions
 */
final public class Restriction extends AbstractCommand {
    final private GamePlayer player;

    public Restriction(GamePlayer player) {
        this.player = player;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("Change the player restrictions")
            .help(
                formatter -> formatter
                    .synopsis("restriction [+/-][restriction]...")

                    .options("+restriction", "Active the restriction on the player")
                    .options("-restriction", "Remove the restriction on the player")

                    .line("AVAILABLE RESTRICTIONS :",
                        "\t" + Arrays.stream(Restrictions.Restriction.values()).map(Enum::name).collect(Collectors.joining(", ")),
                        "\tNote: The name is case insensitive"
                    )
                    .line("WARNING : This is a debug feature, and can cause bugs if misused")

                    .example("restriction +DENY_CHALLENGE", "The player will not be allowed perform challenges")
                    .example("restriction +DENY_CHAT -ALLOW_MOVE_ALL_DIRECTION", "Perform multiple changes")
                    .example("${player:John} restriction +DENY_CHALLENGE", "Change John's restrictions")
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
        List<Restrictions.Restriction> toSet = new ArrayList<>();
        List<Restrictions.Restriction> toUnset = new ArrayList<>();

        for (int i = 1; i < arguments.size(); ++i) {
            String argument = arguments.get(i);

            Restrictions.Restriction restriction = Restrictions.Restriction.valueOf(argument.substring(1).toUpperCase());

            switch (argument.charAt(0)) {
                case '+':
                    toSet.add(restriction);
                    break;

                case '-':
                    toUnset.add(restriction);
                    break;

                default:
                    throw new CommandException(arguments.get(0), "Invalid or missing operation on argument " + argument);
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
