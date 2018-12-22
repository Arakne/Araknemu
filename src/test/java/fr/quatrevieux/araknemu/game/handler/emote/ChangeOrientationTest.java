package fr.quatrevieux.araknemu.game.handler.emote;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.player.Restrictions;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.game.in.emote.SetOrientationRequest;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;
import fr.quatrevieux.araknemu.network.game.out.emote.PlayerOrientation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChangeOrientationTest extends GameBaseCase {
    private ChangeOrientation handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new ChangeOrientation();
    }

    @Test
    void handleSuccess() throws Exception {
        ExplorationPlayer exploration = explorationPlayer();

        handler.handle(session, new SetOrientationRequest(Direction.WEST));

        assertEquals(Direction.WEST, exploration.orientation());

        requestStack.assertLast(new PlayerOrientation(exploration));
    }

    @Test
    void handleWithRestrictedDirection() throws Exception {
        ExplorationPlayer exploration = explorationPlayer();
        exploration.player().restrictions().unset(Restrictions.Restriction.ALLOW_MOVE_ALL_DIRECTION);

        assertErrorPacket(new Noop(), () -> handler.handle(session, new SetOrientationRequest(Direction.WEST)));
        assertEquals(Direction.SOUTH_EAST, exploration.orientation());

        handler.handle(session, new SetOrientationRequest(Direction.SOUTH_WEST));
        assertEquals(Direction.SOUTH_WEST, exploration.orientation());
    }

    @Test
    void functionalNotExploring() {
        assertThrows(CloseImmediately.class, () -> handlePacket(new SetOrientationRequest(Direction.WEST)));
    }

    @Test
    void functionalSuccess() throws Exception {
        ExplorationPlayer exploration = explorationPlayer();
        handlePacket(new SetOrientationRequest(Direction.WEST));

        assertEquals(Direction.WEST, exploration.orientation());
        requestStack.assertLast(new PlayerOrientation(exploration));
    }
}
