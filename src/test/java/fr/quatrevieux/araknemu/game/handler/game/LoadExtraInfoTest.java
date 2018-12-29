package fr.quatrevieux.araknemu.game.handler.game;

import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.network.game.in.game.AskExtraInfo;
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
        dataSet.pushMaps();
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
        player.join(map);

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
    void handleWithFightsInActiveState() throws Exception {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        List<Fight> fights = Arrays.asList(
            createSimpleFight(map),
            createSimpleFight(map)
        );

        fights.forEach(Fight::nextState);

        ExplorationPlayer player = explorationPlayer();
        player.join(map);

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
        player.join(container.get(ExplorationMapService.class).load(10340));

        requestStack.clear();

        handler.handle(session, new AskExtraInfo());

        requestStack.assertAll(
            new AddSprites(
                Arrays.asList(
                    player.sprite(),
                    new GameNpc(
                        new Npc(472, 878, new Position(10340, 82), Direction.SOUTH_EAST),
                        new NpcTemplate(878, 40, 100, 100, Sex.MALE, new Colors(8158389, 13677665, 3683117), "0,20f9,2a5,1d5e,1b9e", 4, 9092)
                    ).sprite()
                )
            ),
            new FightsCount(0),
            new MapReady()
        );
    }
}
