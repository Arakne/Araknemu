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

package fr.quatrevieux.araknemu.game.admin.debug;

import fr.arakne.utils.maps.MapCell;
import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.AdminUser;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.network.game.out.fight.CancelFight;
import fr.quatrevieux.araknemu.network.game.out.game.FightStartPositions;
import org.kohsuke.args4j.Argument;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Show / hide fight places
 */
public final class FightPos extends AbstractCommand<FightPos.Arguments> {
    @Override
    protected void build(Builder builder) {
        builder
            .help(
                formatter -> formatter
                    .description("Show all fight positions")
                    .synopsis("fightpos [show|hide]")
                    .example("fightpos show", "Show the fight positions")
                    .example("fightpos hide", "Hide all fight positions")
            )
            .requires(Permission.DEBUG)
        ;
    }

    @Override
    public String name() {
        return "fightpos";
    }

    @Override
    public void execute(AdminPerformer performer, Arguments arguments) throws AdminException {
        final AdminUser user = AdminUser.class.cast(performer);

        if (arguments.hide()) {
            user.player().exploration().leave();
            user.send(new CancelFight());
            return;
        }

        final ExplorationMap map = user.player().exploration().map();

        if (map == null) {
            performer.error("The player is not on map");
            return;
        }

        final MapCell[][] places = new MapCell[][] {
            map.fightPlaces(0).toArray(new MapCell[0]),
            map.fightPlaces(1).toArray(new MapCell[0]),
        };

        if (places[0].length == 0 || places[1].length == 0) {
            performer.error("No fight places found");
            return;
        }

        performer.info(
            "Places : {} | {}",
            Arrays.stream(places[0]).mapToInt(MapCell::id).mapToObj(Integer::toString).collect(Collectors.joining(", ", "[", "]")),
            Arrays.stream(places[1]).mapToInt(MapCell::id).mapToObj(Integer::toString).collect(Collectors.joining(", ", "[", "]"))
        );

        if (arguments.show()) {
            user.send(new FightStartPositions(places, 0));
        }
    }

    @Override
    public Arguments createArguments() {
        return new Arguments();
    }

    @SuppressWarnings("initialization.field.uninitialized")
    public static final class Arguments {
        @Argument
        private Action action;

        public void setAction(Action action) {
            this.action = action;
        }

        public boolean hide() {
            return action == Action.HIDE;
        }

        public boolean show() {
            return action == Action.SHOW;
        }

        public enum Action {
            SHOW, HIDE
        }
    }
}
