package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.game.world.map.path.Decoder;
import fr.quatrevieux.araknemu.game.world.map.path.Path;
import fr.quatrevieux.araknemu.game.world.map.path.PathStep;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionAcknowledge;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionRequest;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TerminateTurnActionTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter fighter;
    private TerminateTurnAction handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();
        handler = new TerminateTurnAction();
        fighter = player.fighter();

        other.fighter().move(fight.map().get(123));
        fighter.move(fight.map().get(185));
    }

    @Test
    void terminateMove() {
        FightTurn turn = new FightTurn(fighter, fight, Duration.ofSeconds(30));
        turn.start();

        turn.perform(
            new Move(
                turn, fighter,
                new Path<>(
                    new Decoder<>(fight.map()),
                    Arrays.asList(
                        new PathStep<>(fight.map().get(185), Direction.EAST),
                        new PathStep<>(fight.map().get(199), Direction.SOUTH_WEST),
                        new PathStep<>(fight.map().get(213), Direction.SOUTH_WEST),
                        new PathStep<>(fight.map().get(198), Direction.NORTH_WEST)
                    )
                )
            )
        );

        requestStack.clear();

        handler.handle(session, new GameActionAcknowledge(0));

        requestStack.assertAll(
            ActionEffect.usedMovementPoints(fighter, 3),
            "GAF2|1"
        );
        assertEquals(198, fighter.cell().id());
        assertEquals(0, turn.points().movementPoints());
    }
}
