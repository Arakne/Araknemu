package fr.quatrevieux.araknemu.game.fight.turn;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.event.MovementPointsUsed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class FighterTurnPointsTest extends FightBaseCase {
    private Fight fight;
    private Fighter fighter;
    private FighterTurnPoints points;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();

        points = new FighterTurnPoints(fight, fighter);
    }

    @Test
    void defaults() {
        assertEquals(3, points.movementPoints());
    }

    @Test
    void useMovementPoints() {
        AtomicReference<MovementPointsUsed> ref = new AtomicReference<>();
        fight.dispatcher().add(MovementPointsUsed.class, ref::set);

        points.useMovementPoints(2);

        assertSame(fighter, ref.get().fighter());
        assertEquals(2, ref.get().quantity());

        assertEquals(1, points.movementPoints());
    }
}
