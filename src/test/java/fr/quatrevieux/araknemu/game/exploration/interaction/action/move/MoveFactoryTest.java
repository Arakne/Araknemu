package fr.quatrevieux.araknemu.game.exploration.interaction.action.move;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Action;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ExplorationActionRegistry;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator.ValidateWalkable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveFactoryTest extends GameBaseCase {
    private MoveFactory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();
        factory = new MoveFactory(new ValidateWalkable());
    }

    @Test
    void create() throws Exception {
        explorationPlayer().move(explorationPlayer().map().get(100));

        Action action = factory.create(explorationPlayer(), ActionType.MOVE, new String[] {"ebIgbf"});

        assertTrue(action instanceof Move);
        Move move = (Move) action;

        assertEquals(4, move.path().size());
        assertEquals(explorationPlayer().map().get(100), move.path().get(0).cell());
        assertEquals(explorationPlayer().map().get(99), move.path().get(1).cell());
        assertEquals(explorationPlayer().map().get(98), move.path().get(2).cell());
        assertEquals(explorationPlayer().map().get(69), move.path().get(3).cell());
    }

    @Test
    void type() {
        assertSame(ActionType.MOVE, factory.type());
    }

    @Test
    void register() throws Exception {
        explorationPlayer().move(explorationPlayer().map().get(100));
        ExplorationActionRegistry registry = new ExplorationActionRegistry();
        factory.register(registry);

        assertInstanceOf(Move.class, factory.create(explorationPlayer(), ActionType.MOVE, new String[] {"ebIgbf"}));
    }
}
