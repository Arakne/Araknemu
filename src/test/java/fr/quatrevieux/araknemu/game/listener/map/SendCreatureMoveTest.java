package fr.quatrevieux.araknemu.game.listener.map;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.exploration.map.event.CreatureMoving;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.game.world.map.path.Decoder;
import fr.quatrevieux.araknemu.game.world.map.path.Path;
import fr.quatrevieux.araknemu.game.world.map.path.PathStep;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class SendCreatureMoveTest extends GameBaseCase {
    private SendCreatureMove listener;
    private ExplorationPlayer creature;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        creature = explorationPlayer();
        listener = new SendCreatureMove(creature.map());
    }

    @Test
    void onMove() {
        Path<ExplorationMapCell> path = new Path<>(
            new Decoder<>(creature.map()),
            Arrays.asList(
                new PathStep<>(creature.map().get(123), Direction.EAST),
                new PathStep<>(creature.map().get(138), Direction.SOUTH_EAST)
            )
        );

        listener.on(new CreatureMoving(creature, path));

        requestStack.assertLast(new GameActionResponse("", ActionType.MOVE, creature.id(), "ab7bck"));
    }
}
