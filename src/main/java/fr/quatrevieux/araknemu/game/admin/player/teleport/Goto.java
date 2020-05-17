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

package fr.quatrevieux.araknemu.game.admin.player.teleport;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.CommandParser;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.ChangeMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Teleport the player to the desired location
 */
final public class Goto extends AbstractCommand {
    final private GamePlayer player;
    final private ExplorationMapService mapService;
    final private Map<String, LocationResolver> resolvers = new LinkedHashMap<>();

    /**
     * @param player The teleported player
     * @param resolvers Position resolvers
     */
    public Goto(GamePlayer player, ExplorationMapService mapService, LocationResolver[] resolvers) {
        this.player = player;
        this.mapService = mapService;

        for (LocationResolver resolver : resolvers) {
            register(resolver);
        }
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("Teleport the player to the desired location")
            .help(
                formatter -> {
                    formatter
                        .synopsis("goto [type] [target]...")
                        .options("type", "Define the target type (available types are defined bellow). If not provided, will try all available resolvers.")
                        .options("target", "Required. The target. This value depends of the type.")
                        .options("--force", "Force the teleporation even if the player is busy or in fight.")
                    ;

                    for (LocationResolver resolver : resolvers.values()) {
                        formatter.options("type: " + resolver.name(), resolver.help());
                    }

                    formatter
                        .example("goto map 10340", "Teleport to the map id 10340")
                        .example("goto map 10340 cell 45", "Teleport to the map id 10340 at cell 45")
                        .example("goto player John", "Teleport to the John's map")
                        .example("goto position 3;5", "Teleport by geolocation")
                        .example("${player:John} goto map 10340", "Teleport John to map id 10340")
                        .example("${player:John} goto player Alan", "Teleport John to the Alan's map")
                        .example("goto 3;5 cell 42", "Command can works without [type] argument, if not ambiguous")
                    ;
                }
            )
            .requires(Permission.MANAGE_PLAYER)
        ;
    }

    @Override
    public String name() {
        return "goto";
    }

    @Override
    public void execute(AdminPerformer performer, CommandParser.Arguments arguments) throws AdminException {
        final List<String> locationArguments = new ArrayList<>(arguments.arguments().subList(1, arguments.arguments().size()));
        final boolean force = locationArguments.remove("--force");

        if ((!player.isExploring() || player.exploration().interactions().busy()) && !force) {
            throw new AdminException("The player is busy, and cannot be teleported. Use --force to force the teleportation.");
        }

        Target target = parseTarget(locationArguments);

        teleportToTarget(performer, target);
        performer.success("Teleport {} to {}", player.name(), target);
    }

    /**
     * Parse the target from the command arguments
     */
    private Target parseTarget(List<String> arguments) throws AdminException {
        Target target = new Target(
            player.isExploring() ? player.exploration().map() : mapService.load(player.position().map()),
            player.position().cell()
        );

        for (int argIndex = 0; argIndex < arguments.size(); ++argIndex) {
            final String argument = arguments.get(argIndex);

            if (resolvers.containsKey(argument)) {
                ++argIndex;

                if (arguments.size() < argIndex + 1) {
                    throw new AdminException("Missing argument for type " + argument);
                }

                try {
                    resolvers.get(argument).resolve(arguments.get(argIndex), target);
                } catch (IllegalArgumentException e) {
                    throw new AdminException(e);
                }

                continue;
            }

            if (!autoResolve(argument, target)) {
                throw new AdminException("Cannot resolve the argument or type " + argument);
            }
        }

        return target;
    }

    /**
     * Try to auto resolve the argument
     *
     * @param argument The argument to parse
     * @param target The target
     *
     * @return true on success
     */
    private boolean autoResolve(String argument, Target target) {
        for (LocationResolver resolver : resolvers.values()) {
            try {
                resolver.resolve(argument, target);

                return true;
            } catch (IllegalArgumentException e) {
                // ignore resolve exception
            }
        }

        return false;
    }

    /**
     * Perform the teleportation
     *
     * @param performer Command performer
     * @param target The teleportation target
     */
    private void teleportToTarget(AdminPerformer performer, Target target) {
        if (!player.isExploring()) {
            performer.info("Player is not in exploration. Define the position for the next exploration session.");
            player.setPosition(new Position(target.map().id(), target.cell()));
            return;
        }

        if (player.exploration().interactions().busy()) {
            performer.info("Player is busy. Stop all there interactions.");
            player.exploration().interactions().stop();
        }

        player.exploration().interactions().push(new ChangeMap(player.exploration(), target.map(), target.cell()));
    }

    /**
     * Add a new location resolver for the command
     */
    private void register(LocationResolver resolver) {
        resolvers.put(resolver.name(), resolver);
    }
}
