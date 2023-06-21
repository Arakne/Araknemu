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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.handler.game;

import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupPosition;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.npc.NpcService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.team.ConfigurableTeamOptions;
import fr.quatrevieux.araknemu.game.monster.environment.MonsterEnvironmentService;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.network.game.in.game.AskExtraInfo;
import fr.quatrevieux.araknemu.network.game.out.fight.FightOption;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.AddTeamFighters;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.FightsCount;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.ShowFight;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import fr.quatrevieux.araknemu.network.game.out.game.MapReady;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class LoadExtraInfoTest extends FightBaseCase {
    private LoadExtraInfo handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new LoadExtraInfo(container.get(FightService.class));
        dataSet.pushMaps().pushSubAreas().pushAreas();
        login();
        explorationPlayer();
    }

    @Test
    void handleSuccess() throws Exception {
        ExplorationPlayer player = explorationPlayer();
        requestStack.clear();

        handler.handle(session, new AskExtraInfo());

        requestStack.assertAll(
            new AddSprites(
                player.map().sprites()
            ),
            new FightsCount(0),
            new MapReady()
        );
    }

    @Test
    void handleWithFightsInPlacementState() throws Exception {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        List<Fight> fights = Arrays.asList(
            createSimpleFight(map),
            createSimpleFight(map),
            createSimpleFight(map)
        );

        ExplorationPlayer player = explorationPlayer();
        player.changeMap(map, 123);

        requestStack.clear();

        handler.handle(session, new AskExtraInfo());

        requestStack.assertAll(
            new AddSprites(
                player.map().sprites()
            ),

            new ShowFight(fights.get(0)),
            new AddTeamFighters(fights.get(0).team(0)),
            new AddTeamFighters(fights.get(0).team(1)),

            new ShowFight(fights.get(1)),
            new AddTeamFighters(fights.get(1).team(0)),
            new AddTeamFighters(fights.get(1).team(1)),

            new ShowFight(fights.get(2)),
            new AddTeamFighters(fights.get(2).team(0)),
            new AddTeamFighters(fights.get(2).team(1)),

            new FightsCount(3),
            new MapReady()
        );
    }

    @Test
    void handleWithTeamOptions() throws Exception {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        Fight fight = createSimpleFight(map);
        ConfigurableTeamOptions options = (ConfigurableTeamOptions) fight.team(0).options();
        options.toggleNeedHelp();
        options.toggleAllowSpectators();
        options.toggleAllowJoinTeam();

        ExplorationPlayer player = explorationPlayer();
        player.changeMap(map, 123);

        requestStack.clear();

        handler.handle(session, new AskExtraInfo());

        requestStack.assertAll(
            new AddSprites(player.map().sprites()),

            new ShowFight(fight),
            new AddTeamFighters(fight.team(0)),
            new FightOption(fight.team(0).id(), FightOption.Type.BLOCK_JOINER, true),
            new FightOption(fight.team(0).id(), FightOption.Type.BLOCK_SPECTATOR, true),
            new FightOption(fight.team(0).id(), FightOption.Type.NEED_HELP, true),
            new AddTeamFighters(fight.team(1)),

            new FightsCount(1),
            new MapReady()
        );
    }

    @Test
    void handleWithFightsInActiveState() throws Exception {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        List<Fight> fights = Arrays.asList(
            createSimpleFight(map),
            createSimpleFight(map)
        );

        fights.forEach(Fight::nextState);

        ExplorationPlayer player = explorationPlayer();
        player.changeMap(map, 123);

        requestStack.clear();

        handler.handle(session, new AskExtraInfo());

        requestStack.assertAll(
            new AddSprites(
                player.map().sprites()
            ),

            new FightsCount(2),
            new MapReady()
        );
    }

    @Test
    void handleWithNpc() throws Exception {
        dataSet.pushNpcs();

        ExplorationPlayer player = explorationPlayer();
        player.changeMap(container.get(ExplorationMapService.class).load(10340), 123);

        requestStack.clear();

        handler.handle(session, new AskExtraInfo());

        requestStack.assertAll(
            new AddSprites(
                Arrays.asList(
                    player.sprite(),
                    container.get(NpcService.class).get(472).sprite()
                )
            ),
            new FightsCount(0),
            new MapReady()
        );
    }

    @Test
    void handleWithMonsters() throws Exception {
        dataSet
            .pushMonsterSpells()
            .pushMonsterGroups()
            .pushMonsterTemplates()
        ;

        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(10340, -1, 1));

        ExplorationPlayer player = explorationPlayer();
        player.changeMap(container.get(ExplorationMapService.class).load(10340), 123);

        requestStack.clear();

        handler.handle(session, new AskExtraInfo());

        List<MonsterGroup> groups = container.get(MonsterEnvironmentService.class).byMap(10340).stream().findFirst().get().available();

        requestStack.assertAll(
            new AddSprites(
                Arrays.asList(
                    player.sprite(),
                    groups.get(0).sprite(),
                    groups.get(1).sprite()
                )
            ),
            new FightsCount(0),
            new MapReady()
        );
    }
}
