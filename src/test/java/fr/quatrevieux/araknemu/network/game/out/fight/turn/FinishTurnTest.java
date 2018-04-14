package fr.quatrevieux.araknemu.network.game.out.fight.turn;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class FinishTurnTest extends FightBaseCase {
    private Fight fight;
    private FightTurn turn;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        turn = new FightTurn(player.fighter(), fight, Duration.ofMillis(10));
    }

    @Test
    void generate() {
        assertEquals(
            "GTF1",
            new FinishTurn(turn).toString()
        );
    }
}