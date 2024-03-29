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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.data.world.transformer.MonsterListTransformer;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.monster.environment.FixedCellSelector;
import fr.quatrevieux.araknemu.game.monster.environment.LivingMonsterGroupPosition;
import fr.quatrevieux.araknemu.game.monster.environment.MonsterEnvironmentService;
import fr.quatrevieux.araknemu.game.monster.environment.RandomCellSelector;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroupFactory;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spawn a monster group on the map
 */
public final class Spawn extends AbstractCommand<Spawn.Arguments> {
    private final GamePlayer player;
    private final FightService fightService;
    private final MonsterEnvironmentService monsterEnvironmentService;
    private final MonsterGroupFactory groupFactory;
    private final Transformer<List<MonsterGroupData.Monster>> groupParser;

    public Spawn(GamePlayer player, FightService fightService, MonsterEnvironmentService monsterEnvironmentService, MonsterGroupFactory groupFactory) {
        this.player = player;
        this.fightService = fightService;
        this.monsterEnvironmentService = monsterEnvironmentService;
        this.groupFactory = groupFactory;
        this.groupParser = new MonsterListTransformer();
    }

    @Override
    protected void build(AbstractCommand<Arguments>.Builder builder) {
        builder
            .help(
                help -> help
                    .description("Spawn a monster group on the map")
                    .example("spawn 52", "Spawn one arakne")
                    .example("spawn 52x10", "Spawn 10 arakne")
                    .example("spawn 52|54 --size 8", "Spawn a group of arakne and chafer with at most 8 monsters")
                    .example("spawn 52|54 --count 4", "Spawn 4 groups of 1 arakne and 1 chafer")
                    .example("spawn 52|54 --count 4 --move", "Same as above, but monsters will move on the map")
                    .example("spawn --auto", "Respawn a new group on the map")
                    .example("spawn --auto --count 4", "Respawn 4 new group on the map")
                    .example("@John spawn --auto", "Respawn a single group on the map for John")
            )
            .requires(Permission.MANAGE_PLAYER)
            .arguments(Arguments::new)
        ;
    }

    @Override
    public String name() {
        return "spawn";
    }

    @Override
    public void execute(AdminPerformer performer, Arguments arguments) throws AdminException {
        final ExplorationMap map = player.isExploring() ? player.exploration().map() : null;

        if (map == null) {
            error("The player is not on a map");
            return;
        }

        if (arguments.group != null && arguments.auto) {
            error("You should not specify a group and use --auto option at the same time");
            return;
        }

        if (arguments.group != null) {
            manual(performer, map, arguments.group, arguments);
        } else if (arguments.auto) {
            auto(performer, map, arguments);
        } else {
            error("You should specify a group to spawn or use --auto option");
        }
    }

    private void manual(AdminPerformer performer, ExplorationMap map, String groupString, Arguments arguments) throws CommandException {
        final List<MonsterGroupData.Monster> monsters;

        try {
            monsters = groupParser.unserialize(groupString);
        } catch (Exception e) {
            error("Invalid group format : " + e.getMessage());
            return;
        }

        final MonsterGroupData data = new MonsterGroupData(
            -1,
            arguments.respawn,
            arguments.size,
            arguments.count,
            monsters,
            "",
            new Position(0, 0),
            false
        );

        final LivingMonsterGroupPosition position = new LivingMonsterGroupPosition(
            groupFactory,
            monsterEnvironmentService,
            fightService,
            data,
            arguments.move ? new RandomCellSelector() : new FixedCellSelector(player.position().cell()),
            !arguments.move
        );

        try {
            // Populate if required to link the map to the group
            position.populate(map);

            if (arguments.count == 0) {
                position.spawn();
            }

            position.available().forEach(group -> logGroup(performer, group));
        } catch (Exception e) {
            error("Cannot spawn the group : " + e.getMessage());
        }
    }

    private void auto(AdminPerformer performer, ExplorationMap map, Arguments arguments) throws CommandException {
        final Collection<LivingMonsterGroupPosition> groupsOnMap = monsterEnvironmentService.byMap(map.id());

        if (groupsOnMap.isEmpty()) {
            error("The map has no registered groups. Use GROUP argument to define a group to spawn.");
            return;
        }

        for (LivingMonsterGroupPosition position : groupsOnMap) {
            for (int i = 0; i < Math.max(1, arguments.count); ++i) {
                logGroup(performer, position.spawn());
            }
        }
    }

    private void logGroup(AdminPerformer performer, MonsterGroup group) {
        performer.success(
            "The group with {} has been spawned",
            group.monsters().stream()
                .map(monster -> monster.name() + " (id: " + monster.id() + ", level: " + monster.level() + ")")
                .collect(Collectors.joining(", "))
        );
    }

    // @todo "id" option to spawn an already existing monster group
    public static final class Arguments {
        @Argument(
            metaVar = "GROUP",
            usage = "The monster group to spawn.\n" +
                "Format:\n" +
                "[id 1],[level min 1],[level max 1]x[rate1]|[id 2],[level min 2],[level max 2]x[rate2]\n\n" +
                "Monsters are separated by pipe \"|\"\n" +
                "Monster level interval are separated by comma \",\"\n" +
                "Monster spawn rate is an integer that follow \"x\"\n\n" +
                "Levels are not required :\n" +
                "- If not set, all available levels are used\n" +
                "- If only one is set, the level is constant\n" +
                "- If interval is set, only grades into the interval are used\n\n" +
                "The spawn rate is not required, and by default, its value is 1"
        )
        private @Nullable String group;

        @Option(
            name = "--auto",
            aliases = "-a",
            usage = "If set, the monster group of the map will be spawned. When set, the GROUP argument should not be set."
        )
        private boolean auto = false;

        @Option(
            name = "--count",
            aliases = "-c",
            metaVar = "COUNT",
            usage = "Number of groups to spawn. If this value is set, monsters group will respawn automatically. By default only one group will spawn, without respawn."
        )
        private @NonNegative int count = 0;

        @Option(
            name = "--size",
            aliases = "-s",
            metaVar = "SIZE",
            usage = "Maximum number of monsters in the group. By default, all monsters defined in the group will be spawned.",
            forbids = {"--auto"}
        )
        private @NonNegative int size = 0;

        @Option(
            name = "--respawn",
            aliases = "-r",
            metaVar = "DELAY",
            usage = "The respawn delay after a fight. By default, groups will respawn immediately. The option is effective only if the --count option is set.",
            depends = {"--count"},
            forbids = {"--auto"}
        )
        private Duration respawn = Duration.ZERO;

        @Option(
            name = "--move",
            aliases = "-m",
            usage = "If set, groups will spawn on a random cell, and will move randomly on the map. By default, groups are fixed.",
            forbids = {"--auto"}
        )
        private boolean move = false;
    }
}
