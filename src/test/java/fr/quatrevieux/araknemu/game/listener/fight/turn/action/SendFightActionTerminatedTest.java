package fr.quatrevieux.araknemu.game.listener.fight.turn.action;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.event.FightActionTerminated;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.game.world.map.path.Decoder;
import fr.quatrevieux.araknemu.game.world.map.path.Path;
import fr.quatrevieux.araknemu.game.world.map.path.PathStep;
import fr.quatrevieux.araknemu.network.game.out.fight.action.FinishFightAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;

class SendFightActionTerminatedTest extends FightBaseCase {
    private Fight fight;
    private SendFightActionTerminated listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendFightActionTerminated();

        fight = createFight();
        fight.nextState();
        requestStack.clear();
    }

    @Test
    void onFightActionTerminated() {
        Move move = new Move(
            new FightTurn(player.fighter(), fight, Duration.ofSeconds(30)),
            player.fighter(),
            new Path<>(
                new Decoder<>(fight.map()),
                Arrays.asList(
                    new PathStep<>(fight.map().get(185), Direction.EAST),
                    new PathStep<>(fight.map().get(199), Direction.SOUTH_WEST),
                    new PathStep<>(fight.map().get(213), Direction.SOUTH_WEST),
                    new PathStep<>(fight.map().get(198), Direction.NORTH_WEST)
                )
            )
        );

        listener.on(new FightActionTerminated(move));

        requestStack.assertAll(new FinishFightAction(move));
    }

    @Test
    void onFightActionTerminatedWithDeadFighter() {
        player.fighter().life().alter(player.fighter(), -1000);

        Move move = new Move(
            new FightTurn(player.fighter(), fight, Duration.ofSeconds(30)),
            player.fighter(),
            new Path<>(
                new Decoder<>(fight.map()),
                Arrays.asList(
                    new PathStep<>(fight.map().get(185), Direction.EAST),
                    new PathStep<>(fight.map().get(199), Direction.SOUTH_WEST),
                    new PathStep<>(fight.map().get(213), Direction.SOUTH_WEST),
                    new PathStep<>(fight.map().get(198), Direction.NORTH_WEST)
                )
            )
        );

        requestStack.clear();

        listener.on(new FightActionTerminated(move));

        requestStack.assertEmpty();
    }
}