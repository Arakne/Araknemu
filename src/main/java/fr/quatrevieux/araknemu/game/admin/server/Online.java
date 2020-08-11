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
import fr.quatrevieux.araknemu.game.GameService;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.formatter.Link;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;

import java.util.List;
import java.util.stream.Stream;

/**
 * List online players on the current server
 */
final public class Online extends AbstractCommand {
    final private PlayerService service;
    final private ExplorationMapService mapService;
    final private GameService gameService;

    /**
     * Store the command options
     */
    static class Options {
        private int limit = 20;
        private int skip = 0;
        private String search = null;

        public Options(List<String> arguments) {
            for (int i = 1; i < arguments.size(); ++i) {
                switch (arguments.get(i)) {
                    case "--limit":
                        limit = Integer.parseInt(arguments.get(++i));
                        break;

                    case "--skip":
                        skip = Integer.parseInt(arguments.get(++i));
                        break;

                    default:
                        search = arguments.get(i).toLowerCase();
                }
            }
        }

        /**
         * Apply the options on the stream
         */
        public Stream<GamePlayer> apply(Stream<GamePlayer> stream) {
            if (search != null) {
                stream = stream.filter(player -> player.name().toLowerCase().contains(search));
            }

            return stream.skip(skip).limit(limit);
        }
    }

    public Online(PlayerService service, ExplorationMapService mapService, GameService gameService) {
        this.service = service;
        this.mapService = mapService;
        this.gameService = gameService;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("List online players")
            .help(formatter -> formatter
                .synopsis("online [options] [search]")
                .options("search", "Optional. Filter the online player name. Return only players containing the search term into the name.")
                .options("--limit", "Limit the number of returned lines. By default the limit is set to 20.")
                .options("--skip", "Skip the first lines.")
                .example("${server} online", "List all online players")
                .example("${server} online john", "List all online players, containing john in the name")
                .example("${server} online --skip 3 --limit 5 j", "With pagination")
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
        performer.success("There is {} online players with {} active sessions", service.online().size(), gameService.sessions().size());

        Options options = new Options(arguments);

        long count = options
            .apply(service.online().stream())
            .map(this::format)
            .peek(performer::info)
            .count()
        ;

        pagination(performer, options, count);
    }

    /**
     * Format the player line
     */
    private String format(GamePlayer player) {
        return Link.Type.PLAYER.create(player.name()) +
            " " + player.race().name() +
            " " + geolocation(player) +
            " " + state(player) + " [" + ipAddress(player) + "] - " +
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

    /**
     * Render the player IP address
     */
    private String ipAddress(GamePlayer player) {
        return player.account().session()
            .map(session -> session.channel().address().getAddress().getHostAddress())
            .orElse("no ip")
        ;
    }

    /**
     * Display the "next" link
     */
    private void pagination(AdminPerformer performer, Options options, long currentCount) {
        if (currentCount == 0) {
            performer.error("No results found");
            return;
        }

        if (currentCount == options.limit) {
            performer.info(
                "------------------------------------------------\n" +
                "\t<b>" + new Link().execute("${server} online --limit " + options.limit + " --skip " + (options.skip + options.limit)).text("next") + "</b>"
            );
        }
    }
}
