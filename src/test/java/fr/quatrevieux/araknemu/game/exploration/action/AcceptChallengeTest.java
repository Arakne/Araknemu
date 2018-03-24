package fr.quatrevieux.araknemu.game.exploration.action;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.challenge.ChallengeInvitation;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AcceptChallengeTest extends GameBaseCase {
    private ExplorationPlayer player;
    private ExplorationPlayer other;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        player = explorationPlayer();
        other = new ExplorationPlayer(makeOtherPlayer());

        other.join(player.map());
    }

    @Test
    void noInvitation() throws SQLException, ContainerException {
        AcceptChallenge action = new AcceptChallenge(explorationPlayer(), 5);

        assertThrows(IllegalArgumentException.class, () -> action.start(), "Invalid interaction type");
    }

    @Test
    void badTarget() throws Exception {
        other.interactions().start(
            new ChallengeInvitation(other, player)
        );

        AcceptChallenge action = new AcceptChallenge(explorationPlayer(), -5);

        assertThrows(IllegalArgumentException.class, () -> action.start(), "Invalid challenge target");
    }

    @Test
    void initiatorCannotAccept() throws Exception {
        explorationPlayer().interactions().start(new ChallengeInvitation(player, other));

        AcceptChallenge action = new AcceptChallenge(player, player.id());

        assertThrows(IllegalArgumentException.class, () -> action.start());
    }

    @Test
    void successFromChallenger() throws Exception {
        other.interactions().start(new ChallengeInvitation(other, player));

        AcceptChallenge action = new AcceptChallenge(player, other.id());

        action.start();

        assertFalse(player.interactions().interacting());
        assertFalse(other.interactions().interacting());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.ACCEPT_CHALLENGE, "" + player.id(), new Object[] {"" + other.id()})
        );
    }
}
