package fr.quatrevieux.araknemu.game.fight;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectsHandler;
import fr.quatrevieux.araknemu.game.fight.exception.InvalidFightStateException;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.state.NullState;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class FightTest extends GameBaseCase {
    private Fight fight;
    private FightMap map;
    private List<FightTeam> teams;

    private PlayerFighter fighter1, fighter2;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        fight = new Fight(
            new ChallengeType(),
            map = container.get(FightService.class).map(container.get(ExplorationMapService.class).load(10340)),
            teams = Arrays.asList(
                new SimpleTeam(fighter1 = new PlayerFighter(gamePlayer(true)), Arrays.asList(123), 0),
                new SimpleTeam(fighter2 = new PlayerFighter(makeOtherPlayer()), Arrays.asList(321), 1)
            )
        );
    }

    @Test
    void getters() {
        assertEquals(Arrays.asList(fighter1, fighter2), fight.fighters());
        assertSame(map, fight.map());
        assertInstanceOf(NullState.class, fight.state());
        assertEquals(teams, fight.teams());
        assertInstanceOf(ChallengeType.class, fight.type());
        assertInstanceOf(EffectsHandler.class, fight.effects());
    }

    @Test
    void stateBadState() {
        assertThrows(InvalidFightStateException.class, () -> fight.state(PlacementState.class));
    }

    @Test
    void stateWithType() {
        assertInstanceOf(NullState.class, fight.state(NullState.class));

        fight.nextState();

        assertInstanceOf(PlacementState.class, fight.state(PlacementState.class));
    }

    @Test
    void teamByNumber() {
        assertSame(teams.get(0), fight.team(0));
        assertSame(teams.get(1), fight.team(1));
    }

    @Test
    void send() {
        fight.send("test");

        requestStack.assertLast("test");
    }

    @Test
    void schedule() throws InterruptedException {
        AtomicBoolean ab = new AtomicBoolean(false);

        fight.schedule(() -> ab.set(true), Duration.ofMillis(10));

        assertFalse(ab.get());

        Thread.sleep(11);
        assertTrue(ab.get());
    }

    @Test
    void execute() throws InterruptedException {
        AtomicBoolean ab = new AtomicBoolean(false);

        fight.execute(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ab.set(true);
        });

        assertFalse(ab.get());

        Thread.sleep(11);
        assertTrue(ab.get());
    }
}