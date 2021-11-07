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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin.server;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.admin.executor.argument.handler.ConcatRestOfArgumentsHandler;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.network.out.ServerMessage;
import inet.ipaddr.IPAddressString;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import java.util.function.Predicate;

/**
 * Command for kick multiple players
 */
public final class Kick extends AbstractCommand<Kick.Arguments> {
    /**
     * Base filter : skip all game master from kick
     */
    private static final Predicate<GamePlayer> GAME_MASTER_FILTER = player -> !player.account().isMaster();

    private final PlayerService service;

    public Kick(PlayerService service) {
        this.service = service;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .help(help -> help
                .description("Kick many players from current game server")
                .synopsis("kick [options] [MESSAGE]")
                .line("Note: At least one option is required. Use --all to kick all players.")

                .example("kick --all", "Kick all players (expect game masters)")
                .example("kick --ip 15.18.0.0/16", "Kick all players from an ip mask")
                .example("kick --ip 15.18.1.23", "Kick all players from an ip")

                .seeAlso("banip", "For ban an IP address")
                .seeAlso("!kick", "For kick a player")
            )
            .requires(Permission.MANAGE_PLAYER)
            .arguments(Arguments::new)
        ;
    }

    @Override
    public String name() {
        return "kick";
    }

    @Override
    public void execute(AdminPerformer performer, Arguments arguments) throws AdminException {
        final ServerMessage packet = ServerMessage.kick(
            performer.account().map(GameAccount::pseudo).orElse("system"),
            arguments.message.isEmpty() ? "" : "\n" + arguments.message
        );

        final long count = service
            .filter(arguments.createFilter())
            .map(GamePlayer::account)
            .peek(account -> account.kick(packet))
            .count()
        ;

        performer.success("{} player(s) kicked from server", count);
    }

    public static final class Arguments {
        @Option(name = "--all", usage = "Kick all players", forbids = {"--ip"})
        private boolean all;

        @Option(name = "--ip", metaVar = "IP_ADDRESS", usage = "Kick by IP address mask")
        private IPAddressString ip;

        @Argument(metaVar = "MESSAGE", handler = ConcatRestOfArgumentsHandler.class, usage = "The message to send. Must be defined after all options.")
        private String message = "";

        /**
         * Create the filter predicate from options
         *
         * @return The predicate
         * @throws CommandException When invalid options given
         */
        public Predicate<GamePlayer> createFilter() throws CommandException {
            Predicate<GamePlayer> filter = GAME_MASTER_FILTER;

            if (all) {
                return filter;
            }

            filter = applyIp(filter);

            if (filter == GAME_MASTER_FILTER) {
                throw new CommandException("kick", "At least one filter must be defined, or use --all to kick all players");
            }

            return filter;
        }

        private Predicate<GamePlayer> applyIp(Predicate<GamePlayer> filter) {
            if (ip == null) {
                return filter;
            }

            return filter.and(
                player -> player.account().session()
                    .map(session -> session.channel().address().getAddress().getHostAddress())
                    .map(IPAddressString::new)
                    .filter(sessionIp -> ip.contains(sessionIp))
                    .isPresent()
            );
        }
    }
}
