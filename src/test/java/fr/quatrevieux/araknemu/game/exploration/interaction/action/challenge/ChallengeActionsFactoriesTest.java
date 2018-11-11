package fr.quatrevieux.araknemu.game.exploration.interaction.action.challenge;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Action;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ExplorationActionRegistry;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ChallengeActionsFactoriesTest extends FightBaseCase {
    private ExplorationActionRegistry factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        factory = new ExplorationActionRegistry(new ChallengeActionsFactories(container.get(FightService.class)));

        dataSet.pushMaps();
    }

    @Test
    void ask() throws Exception {
        ExplorationPlayer other = new ExplorationPlayer(this.other);

        explorationPlayer().map().add(other);

        Action action = factory.create(explorationPlayer(), ActionType.CHALLENGE, new String[] {"" + other.id()});

        assertInstanceOf(AskChallenge.class, action);
        assertSame(explorationPlayer(), action.performer());
        assertSame(ActionType.CHALLENGE, action.type());
        assertArrayEquals(new Object[] {other.id()}, action.arguments());
    }

    @Test
    void accept() throws Exception {
        ExplorationPlayer other = new ExplorationPlayer(this.other);

        explorationPlayer().map().add(other);

        Action action = factory.create(explorationPlayer(), ActionType.ACCEPT_CHALLENGE, new String[] {"" + other.id()});

        assertInstanceOf(AcceptChallenge.class, action);
        assertSame(explorationPlayer(), action.performer());
        assertSame(ActionType.ACCEPT_CHALLENGE, action.type());
        assertArrayEquals(new Object[] {other.id()}, action.arguments());
    }

    @Test
    void refuse() throws Exception {
        ExplorationPlayer other = new ExplorationPlayer(this.other);

        explorationPlayer().map().add(other);

        Action action = factory.create(explorationPlayer(), ActionType.REFUSE_CHALLENGE, new String[] {"" + other.id()});

        assertInstanceOf(RefuseChallenge.class, action);
        assertSame(explorationPlayer(), action.performer());
        assertSame(ActionType.REFUSE_CHALLENGE, action.type());
        assertArrayEquals(new Object[] {other.id()}, action.arguments());
    }
}
