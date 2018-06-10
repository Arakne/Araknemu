package fr.quatrevieux.araknemu.game.fight.builder;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightHandler;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.AddTeamFighters;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.FightsCount;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.HideFight;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.ShowFight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertSame;

class FightHandlerTest extends GameBaseCase {
    private FightService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        service = container.get(FightService.class);
    }

    @Test
    void withChallengeFight() throws ContainerException {
        FightHandler<ChallengeBuilder> handler = new FightHandler<>(
            container.get(FightService.class),
            new ChallengeBuilder(container.get(FightService.class))
        );

        Fight fight = handler.start(
            builder -> {
                try {
                    builder
                        .map(container.get(ExplorationMapService.class).load(10340))
                        .fighter(gamePlayer())
                        .fighter(makeOtherPlayer())
                    ;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        );

        assertInstanceOf(ChallengeType.class, fight.type());
        assertInstanceOf(PlacementState.class, fight.state());
        assertCount(2, fight.teams());
        assertCount(2, fight.fighters());
    }

    @Test
    void displayOnMap() throws SQLException, ContainerException {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        explorationPlayer().join(map);
        requestStack.clear();

        FightHandler<ChallengeBuilder> handler = new FightHandler<>(
            service,
            new ChallengeBuilder(service)
        );

        Fight fight = handler.start(
            builder -> {
                try {
                    builder
                        .map(map)
                        .fighter(makeSimpleGamePlayer(5))
                        .fighter(makeSimpleGamePlayer(6))
                    ;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        );

        assertSame(fight, service.getFromMap(10340, 1));

        requestStack.assertAll(
            new ShowFight(fight),
            new AddTeamFighters(fight.team(0)),
            new AddTeamFighters(fight.team(1)),
            new FightsCount(1)
        );

        requestStack.clear();

        fight.start();
        requestStack.assertLast(new HideFight(fight));

        fight.stop();
        requestStack.assertLast(new FightsCount(0));

        assertCount(0, service.fightsByMap(10340));
    }

    @Test
    void cancelFightWillRemoveTheFight() throws ContainerException {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        FightHandler<ChallengeBuilder> handler = new FightHandler<>(
            service,
            new ChallengeBuilder(service)
        );

        Fight fight = handler.start(
            builder -> {
                try {
                    builder
                        .map(map)
                        .fighter(makeSimpleGamePlayer(5))
                        .fighter(makeSimpleGamePlayer(6))
                    ;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        );

        fight.cancel();
        assertCount(0, service.fightsByMap(10340));
    }
}
