package fr.quatrevieux.araknemu.game.fight.state;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.game.listener.fight.SendFightStarted;
import fr.quatrevieux.araknemu.game.listener.fight.turn.SendFightTurnStarted;
import fr.quatrevieux.araknemu.game.listener.fight.turn.SendFightTurnStopped;
import fr.quatrevieux.araknemu.game.listener.fight.turn.SendFightersInformation;
import fr.quatrevieux.araknemu.network.game.out.fight.BeginFight;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FighterTurnOrder;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.StartTurn;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.TurnMiddle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ActiveStateTest extends GameBaseCase {
    private ActiveState state;
    private Fight fight;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        fight = new Fight(
            new ChallengeType(),
            container.get(FightService.class).map(container.get(ExplorationMapService.class).load(10340)),
            Arrays.asList(
                new SimpleTeam(fighter = new PlayerFighter(gamePlayer(true)), Arrays.asList(123, 222), 0),
                new SimpleTeam(new PlayerFighter(makeOtherPlayer()), Arrays.asList(321), 1)
            ),
            new StatesFlow(
                new NullState(),
                new NullState(),
                state = new ActiveState()
            )
        );

        new InitialiseState(false).start(fight);
        requestStack.clear();
    }

    @Test
    void start() throws InterruptedException {
        state.start(fight);

        assertTrue(fight.dispatcher().has(SendFightStarted.class));
        assertTrue(fight.dispatcher().has(SendFightersInformation.class));
        assertTrue(fight.dispatcher().has(SendFightTurnStarted.class));
        assertTrue(fight.dispatcher().has(SendFightTurnStopped.class));

        Thread.sleep(210); // Wait for start turn

        assertTrue(fight.turnList().current().isPresent());

        requestStack.assertAll(
            new BeginFight(),
            new FighterTurnOrder(fight.turnList()),
            new TurnMiddle(fight.fighters()),
            new StartTurn(fight.turnList().current().get())
        );
    }
}
