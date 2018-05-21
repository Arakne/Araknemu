package fr.quatrevieux.araknemu.game.fight.team;

import fr.quatrevieux.araknemu.data.constant.Alignment;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SimpleTeamTest extends FightBaseCase {
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
        assertEquals(1, team.id());
        assertEquals(0, team.type());
        assertEquals(Alignment.NONE, team.alignment());
        assertEquals(player.position().cell(), team.cell());
    }

    @Test
    void send() {
        team.send("test");

        requestStack.assertLast("test");
    }

    @Test
    void alive() throws Exception {
        assertTrue(team.alive());

        fighter.init();
        fighter.setFight(createFight());
        fighter.life().alter(fighter, -1000);

        assertFalse(team.alive());
    }
}