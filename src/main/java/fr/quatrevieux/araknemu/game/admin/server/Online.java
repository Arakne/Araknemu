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
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import java.util.stream.Stream;

/**
 * List online players on the current server
 */
public final class Online extends AbstractCommand<Online.Arguments> {
    private final PlayerService service;
    private final ExplorationMapService mapService;
    private final GameService gameService;

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
    public void execute(AdminPerformer performer, Arguments arguments) {
        performer.success("There is {} online players with {} active sessions", service.online().size(), gameService.sessions().size());

        final long count = arguments
            .apply(service.online().stream())
            .map(this::format)
            .peek(performer::info)
            .count()
        ;

        pagination(performer, arguments, count);
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
    private void pagination(AdminPerformer performer, Arguments arguments, long currentCount) {
        if (currentCount == 0) {
            performer.error("No results found");
            return;
        }

        if (currentCount == arguments.limit) {
            performer.info(
                "------------------------------------------------\n" +
                "\t<b>" + new Link().execute("${server} online --limit " + arguments.limit + " --skip " + (arguments.skip + arguments.limit)).text("next") + "</b>"
            );
        }
    }

    @Override
    public Arguments createArguments() {
        return new Arguments();
    }

    /**
     * Store the command options
     */
    public static class Arguments {
        @Option(name = "--limit", usage = "Limit the number of returned lines. By default the limit is set to 20.")
        private int limit = 20;

        @Option(name = "--skip", usage = "Skip the first lines.")
        private int skip = 0;

        @Argument(metaVar = "SEARCH", usage = "Optional. Filter the online player name. Return only players containing the search term into the name.")
        private String search = null;

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
}
