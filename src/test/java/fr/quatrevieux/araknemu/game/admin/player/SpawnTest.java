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

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupPosition;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.monster.environment.LivingMonsterGroupPosition;
import fr.quatrevieux.araknemu.game.monster.environment.MonsterEnvironmentService;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroupFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpawnTest extends CommandTestCase {
    private ExplorationMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        command = new Spawn(
            gamePlayer(),
            container.get(FightService.class),
            container.get(MonsterEnvironmentService.class),
            container.get(MonsterGroupFactory.class)
        );

        dataSet
            .pushMonsterTemplates()
            .pushMonsterSpells()
            .pushMonsterGroups()
            .pushMonsterGroupPosition(new MonsterGroupPosition(10340, -1, 3))
        ;

        explorationPlayer().leave();
        explorationPlayer().changeMap(container.get(ExplorationMapService.class).load(10340), 123);
        map = explorationPlayer().map();

        groupsOnMap().forEach(map::remove); // Clean map
    }

    @Test
    void executeSingleMonster() throws ContainerException, SQLException, AdminException {
        execute("spawn", "34,7");

        List<MonsterGroup> groups = groupsOnMap();

        assertCount(1, groups);
        assertCount(1, groups.get(0).monsters());
        assertEquals(34, groups.get(0).monsters().get(0).id());
        assertTrue(groups.get(0).handler().fixed());

        assertOutput("The group with Larve Verte (id: 34, level: 7) has been spawned");
    }

    @Test
    void executeMultipleMonsters() throws ContainerException, SQLException, AdminException {
        execute("spawn", "31,2|34,7");

        List<MonsterGroup> groups = groupsOnMap();

        assertCount(1, groups);
        assertCount(2, groups.get(0).monsters());
        assertEquals(31, groups.get(0).monsters().get(0).id());
        assertEquals(34, groups.get(0).monsters().get(1).id());

        assertOutput("The group with Larve Bleue (id: 31, level: 2), Larve Verte (id: 34, level: 7) has been spawned");
    }

    @Test
    void executeCountOption() throws ContainerException, SQLException, AdminException {
        execute("spawn", "31,2", "--count", "4");

        List<MonsterGroup> groups = groupsOnMap();

        assertCount(4, groups);
        assertEquals(31, groups.get(0).monsters().get(0).id());
        assertEquals(31, groups.get(1).monsters().get(0).id());
        assertEquals(31, groups.get(2).monsters().get(0).id());
        assertEquals(31, groups.get(3).monsters().get(0).id());

        assertOutput(
            "The group with Larve Bleue (id: 31, level: 2) has been spawned",
            "The group with Larve Bleue (id: 31, level: 2) has been spawned",
            "The group with Larve Bleue (id: 31, level: 2) has been spawned",
            "The group with Larve Bleue (id: 31, level: 2) has been spawned"
        );
    }

    @Test
    void executeSizeOption() throws ContainerException, SQLException, AdminException {
        int maxSize = 0;

        for (int i = 0; i < 10; ++i) {
            execute("spawn", "31,2", "--size", "8");

            List<MonsterGroup> groups = groupsOnMap();

            assertCount(1, groups);

            if (groups.get(0).monsters().size() > maxSize) {
                maxSize = groups.get(0).monsters().size();
            }

            map.remove(groups.get(0));
        }

        assertEquals(8, maxSize);
    }

    @Test
    void executeMoveOption() throws ContainerException, SQLException, AdminException {
        execute("spawn", "34,7", "--move");

        List<MonsterGroup> groups = groupsOnMap();

        assertCount(1, groups);
        assertCount(1, groups.get(0).monsters());
        assertEquals(34, groups.get(0).monsters().get(0).id());
        assertFalse(groups.get(0).handler().fixed());

        assertOutput("The group with Larve Verte (id: 34, level: 7) has been spawned");
    }

    @Test
    void executeRespawnOption() throws ContainerException, SQLException, AdminException, NoSuchFieldException, IllegalAccessException {
        execute("spawn", "34,7", "--count", "1", "--respawn", "10m");

        List<MonsterGroup> groups = groupsOnMap();

        assertCount(1, groups);
        assertCount(1, groups.get(0).monsters());
        assertEquals(34, groups.get(0).monsters().get(0).id());

        Field field = LivingMonsterGroupPosition.class.getDeclaredField("data");
        field.setAccessible(true);

        MonsterGroupData data = (MonsterGroupData) field.get(groups.get(0).handler());
        assertEquals(Duration.ofMinutes(10), data.respawnTime());

        assertOutput("The group with Larve Verte (id: 34, level: 7) has been spawned");
    }

    @Test
    void autoWithoutGroupOnMap() throws AdminException, SQLException {
        explorationPlayer().leave();
        explorationPlayer().changeMap(container.get(ExplorationMapService.class).load(10300), 123);
        assertThrowsWithMessage(CommandException.class, "The map has no registered groups. Use GROUP argument to define a group to spawn.", () -> execute("spawn", "--auto"));
    }

    @Test
    void autoWithSingleGroup() throws AdminException, SQLException {
        int count = groupsOnMap().size();

        execute("spawn", "--auto");

        List<MonsterGroup> groups = groupsOnMap();

        assertCount(count + 1, groups);
        assertCount(1, groups.get(0).monsters());
        assertEquals(36, groups.get(0).monsters().get(0).id());

        assertOutputRegex("The group with Bouftou \\(id: 36, level: \\d+\\) has been spawned");
    }

    @Test
    void autoWithCount() throws AdminException, SQLException {
        int count = groupsOnMap().size();

        execute("spawn", "--auto", "--count", "5");

        List<MonsterGroup> groups = groupsOnMap();

        assertCount(count + 5, groups);

        assertOutputRegex(
            "The group with Bouftou \\(id: 36, level: \\d+\\) has been spawned",
            "The group with Bouftou \\(id: 36, level: \\d+\\) has been spawned",
            "The group with Bouftou \\(id: 36, level: \\d+\\) has been spawned",
            "The group with Bouftou \\(id: 36, level: \\d+\\) has been spawned",
            "The group with Bouftou \\(id: 36, level: \\d+\\) has been spawned"
        );
    }

    @Test
    void executeMissingParameter() throws ContainerException, SQLException, AdminException {
        assertThrowsWithMessage(CommandException.class, "You should specify a group to spawn or use --auto option", () -> execute("spawn"));
    }

    @Test
    void notOnMap() throws ContainerException, SQLException, AdminException {
        explorationPlayer().leave();

        assertThrowsWithMessage(CommandException.class, "The player is not on a map", () -> execute("spawn", "36"));
    }

    @Test
    void cannotUseGroupAndAuto() throws ContainerException, SQLException, AdminException {
        assertThrowsWithMessage(CommandException.class, "You should not specify a group and use --auto option at the same time", () -> execute("spawn", "36",  "--auto"));
    }

    @Test
    void autoIncompatibleOptions() throws ContainerException, SQLException, AdminException {
        assertThrowsWithMessage(CommandException.class, "option \"--size (-s)\" cannot be used with the option(s) [--auto]", () -> execute("spawn", "--auto", "--size", "5"));
        assertThrowsWithMessage(CommandException.class, "option \"--respawn (-r)\" requires the option(s) [--count]", () -> execute("spawn", "--auto", "--respawn", "5s"));
        assertThrowsWithMessage(CommandException.class, "option \"--move (-m)\" cannot be used with the option(s) [--auto]", () -> execute("spawn", "--auto", "--move"));
    }

    @Test
    void invalidGroupString() throws ContainerException, SQLException, AdminException {
        assertThrowsWithMessage(CommandException.class, "Invalid group format : For input string: \"invalid\"", () -> execute("spawn", "invalid"));
        assertThrowsWithMessage(CommandException.class, "Cannot spawn the group : Monster 404 is not found", () -> execute("spawn", "404"));
        assertThrowsWithMessage(CommandException.class, "Cannot spawn the group : Cannot found a free cell on map 10340", () -> execute("spawn", "36", "--count", "1000", "--move"));
    }

    @Test
    void help() {
        assertHelp(
            "spawn - Spawn a monster group on the map",
            "========================================",
            "SYNOPSIS",
                "\tspawn [GROUP] [--auto (-a)] [--count (-c) COUNT] [--move (-m)] [--respawn (-r) DELAY] [--size (-s) SIZE]",
            "OPTIONS",
                "\tGROUP : The monster group to spawn.",
                    "\t\tFormat:",
                    "\t\t[id 1],[level min 1],[level max 1]x[rate1]|[id 2],[level min 2],[level max 2]x[rate2]",
                    "\t\t",
                    "\t\tMonsters are separated by pipe \"|\"",
                    "\t\tMonster level interval are separated by comma \",\"",
                    "\t\tMonster spawn rate is an integer that follow \"x\"",
                    "\t\t",
                    "\t\tLevels are not required :",
                    "\t\t- If not set, all available levels are used",
                    "\t\t- If only one is set, the level is constant",
                    "\t\t- If interval is set, only grades into the interval are used",
                    "\t\t",
                    "\t\tThe spawn rate is not required, and by default, its value is 1",
                "\t--auto (-a) : If set, the monster group of the map will be spawned. When set, the GROUP argument should not be set.",
                "\t--count (-c) : Number of groups to spawn. If this value is set, monsters group will respawn automatically. By default only one group will spawn, without respawn.",
                "\t--move (-m) : If set, groups will spawn on a random cell, and will move randomly on the map. By default, groups are fixed.",
                "\t--respawn (-r) : The respawn delay after a fight. By default, groups will respawn immediately. The option is effective only if the --count option is set.",
                "\t--size (-s) : Maximum number of monsters in the group. By default, all monsters defined in the group will be spawned.",
            "EXAMPLES",
                "\tspawn 52                     - Spawn one arakne",
                "\tspawn 52x10                  - Spawn 10 arakne",
                "\tspawn 52|54 --size 8         - Spawn a group of arakne and chafer with at most 8 monsters",
                "\tspawn 52|54 --count 4        - Spawn 4 groups of 1 arakne and 1 chafer",
                "\tspawn 52|54 --count 4 --move - Same as above, but monsters will move on the map",
                "\tspawn --auto                 - Respawn a new group on the map",
                "\tspawn --auto --count 4       - Respawn 4 new group on the map",
                "\t@John spawn --auto           - Respawn a single group on the map for John",
            "PERMISSIONS",
                "\t[ACCESS, MANAGE_PLAYER]"
        );
    }

    private List<MonsterGroup> groupsOnMap() {
        return map.creatures().stream()
            .filter(MonsterGroup.class::isInstance)
            .map(MonsterGroup.class::cast)
            .collect(Collectors.toList())
        ;
    }
}
