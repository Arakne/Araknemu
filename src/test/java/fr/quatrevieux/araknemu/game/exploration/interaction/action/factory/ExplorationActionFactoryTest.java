package fr.quatrevieux.araknemu.game.exploration.interaction.action.factory;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.*;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.fight.JoinFight;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExplorationActionFactoryTest extends FightBaseCase {
    private ExplorationActionFactory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        factory = new ExplorationActionFactory(
            container.get(FightService.class)
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
    void createMove() throws Exception {
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
    void createAskChallenge() throws Exception {
        ExplorationPlayer other = new ExplorationPlayer(this.other);

        explorationPlayer().map().add(other);

        Action action = factory.create(explorationPlayer(), ActionType.CHALLENGE, new String[] {"" + other.id()});

        assertInstanceOf(AskChallenge.class, action);
    }

    @Test
    void createAcceptChallenge() throws Exception {
        ExplorationPlayer other = new ExplorationPlayer(this.other);

        explorationPlayer().map().add(other);

        Action action = factory.create(explorationPlayer(), ActionType.ACCEPT_CHALLENGE, new String[] {"" + other.id()});

        assertInstanceOf(AcceptChallenge.class, action);
    }

    @Test
    void createRefuseChallenge() throws Exception {
        ExplorationPlayer other = new ExplorationPlayer(this.other);

        explorationPlayer().map().add(other);

        Action action = factory.create(explorationPlayer(), ActionType.REFUSE_CHALLENGE, new String[] {"" + other.id()});

        assertInstanceOf(RefuseChallenge.class, action);
    }

    @Test
    void createJoinFight() throws Exception {
        ExplorationPlayer player = explorationPlayer();

        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);
        player.join(map);

        Fight fight = createSimpleFight(map);

        Action action = factory.create(player, ActionType.JOIN_FIGHT, new String[] {fight.id() + "", fight.team(0).id() + ""});

        assertInstanceOf(JoinFight.class, action);
    }
}
