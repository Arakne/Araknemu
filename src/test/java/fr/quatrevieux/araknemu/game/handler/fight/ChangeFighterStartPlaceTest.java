package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.state.InitialiseState;
import fr.quatrevieux.araknemu.game.fight.state.NullState;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.state.StatesFlow;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.in.fight.FighterChangePlace;
import fr.quatrevieux.araknemu.network.game.out.fight.ChangeFighterPlaceError;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterPositions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ChangeFighterStartPlaceTest extends GameBaseCase {
    private ChangeFighterStartPlace handler;
    private Fight fight;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        fight = new Fight(
            1,
            new ChallengeType(),
            container.get(FightService.class).map(container.get(ExplorationMapService.class).load(10340)),
            Arrays.asList(
                new SimpleTeam(fighter = new PlayerFighter(gamePlayer(true)), Arrays.asList(123, 222), 0),
                new SimpleTeam(new PlayerFighter(makeOtherPlayer()), Arrays.asList(321), 1)
            ),
            new StatesFlow(
                new NullState(),
                new InitialiseState(),
                new PlacementState(false)
            )
        );

        fight.nextState();

        handler = new ChangeFighterStartPlace();
    }

    @Test
    void handleError() throws Exception {
        try {
            handler.handle(session, new FighterChangePlace(256));
            fail("ErrorPacket must be thrown");
        } catch (ErrorPacket e) {
            assertEquals(
                new ChangeFighterPlaceError().toString(),
                e.packet().toString()
            );
        }
    }

    @Test
    void handleSuccess() throws Exception {
        handler.handle(session, new FighterChangePlace(222));

        requestStack.assertLast(new FighterPositions(fight.fighters()));

        assertEquals(222, fighter.cell().id());
    }
}
