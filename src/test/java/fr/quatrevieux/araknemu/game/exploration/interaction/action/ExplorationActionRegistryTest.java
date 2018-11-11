package fr.quatrevieux.araknemu.game.exploration.interaction.action;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.challenge.ChallengeActionsFactories;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.fight.FightActionsFactories;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.Move;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.MoveFactory;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExplorationActionRegistryTest extends FightBaseCase {
    private ExplorationActionRegistry factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        factory = new ExplorationActionRegistry(
            new MoveFactory(),
            new ChallengeActionsFactories(container.get(FightService.class)),
            new FightActionsFactories(container.get(FightService.class))
        );

        dataSet.pushMaps();
    }

    @Test
    void createNotFound() {
        assertThrows(
            Exception.class,
            () -> factory.create(explorationPlayer(), ActionType.NONE, new String[] {}),
            "No factory found for game action : NONE"
        );
    }

    @Test
    void createSuccess() throws Exception {
        explorationPlayer().move(explorationPlayer().map().get(100));

        Action action = factory.create(explorationPlayer(), ActionType.MOVE, new String[] {"ebIgbf"});

        assertInstanceOf(Move.class, action);
    }

    @Test
    void register() throws Exception {
        Action action = Mockito.mock(Action.class);
        factory.register(ActionType.NONE, (player, type, arguments) -> action);

        assertSame(action, factory.create(explorationPlayer(), ActionType.NONE, new String[] {}));
    }
}
