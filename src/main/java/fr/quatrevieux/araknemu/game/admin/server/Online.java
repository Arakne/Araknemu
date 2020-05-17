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

package fr.quatrevieux.araknemu.game.admin.server;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.data.value.Geolocation;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.formatter.Link;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;

import java.util.List;

/**
 * List online players on the current server
 */
final public class Online extends AbstractCommand {
    final private PlayerService service;
    final private ExplorationMapService mapService;

    public Online(PlayerService service, ExplorationMapService mapService) {
        this.service = service;
        this.mapService = mapService;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("List online players")
            .help(formatter -> formatter
                .synopsis("online [search]")
                .options("search", "Optional. Filter the online player name. Return only players containing the search term into the name.")
                .example("${server} online", "List all online players")
                .example("${server} online john", "List all online players, containing john in the name")
            )
            .requires(Permission.MANAGE_PLAYER)
        ;
    }

    @Override
    public String name() {
        return "online";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) {
        performer.success("There is {} online players", service.online().size());

        service
            .filter(player -> arguments.size() <= 1 || player.name().toLowerCase().contains(arguments.get(1).toLowerCase()))
            .map(this::format)
            .forEach(performer::info)
        ;
    }

    /**
     * Format the player line
     */
    private String format(GamePlayer player) {
        return Link.Type.PLAYER.create(player.name()) +
            " " + player.race().name() +
            " " + geolocation(player) +
            " " + state(player) + " - " +
            Link.Type.EXECUTE.create("${player:" + player.name() + "} info").text("info") +
            " " + Link.Type.EXECUTE.create("goto player " + player.name()).text("goto")
        ;
    }

    /**
     * Get the player geolocation
     */
    private Geolocation geolocation(GamePlayer player) {
        if (player.isExploring() && player.exploration().map() != null) {
            return player.exploration().map().geolocation();
        }

        return mapService.load(player.position().map()).geolocation();
    }

    /**
     * Get the player game state as string
     */
    private String state(GamePlayer player) {
        if (player.isFighting()) {
            return "in combat";
        }

        if (player.isExploring()) {
            return "in exploration";
        }

        return "joining game";
    }
}
