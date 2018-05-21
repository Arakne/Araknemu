package fr.quatrevieux.araknemu.game.handler.game;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
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
}
