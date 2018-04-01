package fr.quatrevieux.araknemu.game.fight.state;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class InitialiseStateTest extends GameBaseCase {
    private Fight fight;
    private FightState nextState;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        fight = new Fight(
            new ChallengeType(),
            container.get(FightService.class).map(container.get(ExplorationMapService.class).load(10340)),
            Arrays.asList(
                new SimpleTeam(new PlayerFighter(gamePlayer()), Arrays.asList(123), 0),
                new SimpleTeam(new PlayerFighter(makeOtherPlayer()), Arrays.asList(321), 1)
            ),
            new StatesFlow(
                new NullState(),
                nextState = new NullState()
            )
        );
    }

    @Test
    void start() {
        new InitialiseState().start(fight);

        assertSame(fight, fight.fighters().get(0).fight());
        assertSame(fight, fight.fighters().get(1).fight());

        assertSame(fight.team(0), fight.fighters().get(0).team());
        assertSame(fight.team(1), fight.fighters().get(1).team());

        assertEquals(123, fight.fighters().get(0).cell().id());
        assertEquals(321, fight.fighters().get(1).cell().id());

        assertSame(nextState, fight.state());
    }

    @Test
    void id() {
        assertEquals(1, new InitialiseState().id());
    }
}
