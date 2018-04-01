package fr.quatrevieux.araknemu.game.fight.team;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayerFighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SimpleTeamTest extends GameBaseCase {
    private SimpleTeam team;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        team = new SimpleTeam(
            fighter = new PlayerFighter(gamePlayer(true)),
            Arrays.asList(123, 456),
            1
        );
    }

    @Test
    void getters() {
        assertEquals(Arrays.asList(fighter), team.fighters());
        assertEquals(Arrays.asList(123, 456), team.startPlaces());
        assertEquals(1, team.number());
    }

    @Test
    void send() {
        team.send("test");

        requestStack.assertLast("test");
    }
}