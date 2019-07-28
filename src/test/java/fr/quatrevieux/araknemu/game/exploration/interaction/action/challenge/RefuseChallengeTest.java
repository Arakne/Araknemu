package fr.quatrevieux.araknemu.game.exploration.interaction.action.challenge;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionQueue;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.challenge.ChallengeInvitationHandler;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RefuseChallengeTest extends GameBaseCase {
    private ExplorationPlayer player;
    private ExplorationPlayer other;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        player = explorationPlayer();
        other = new ExplorationPlayer(makeOtherPlayer());

        player.join(container.get(ExplorationMapService.class).load(10340));
        other.join(player.map());
    }

    @Test
    void noInvitation() throws SQLException, ContainerException {
        RefuseChallenge action = new RefuseChallenge(explorationPlayer(), 5);

        assertThrows(IllegalArgumentException.class, () -> action.start(new ActionQueue()), "Invalid interaction type");
    }

    @Test
    void badTarget() throws Exception {
        player.interactions().start(invitationHandler().invitation(other, player));

        RefuseChallenge action = new RefuseChallenge(explorationPlayer(), -5);

        assertThrows(IllegalArgumentException.class, () -> action.start(new ActionQueue()), "Invalid challenge target");
    }

    @Test
    void successFromInitiator() throws Exception {
        explorationPlayer().interactions().start(invitationHandler().invitation(player, other));

        RefuseChallenge action = new RefuseChallenge(player, player.id());

        action.start(new ActionQueue());

        assertFalse(player.interactions().interacting());
        assertFalse(other.interactions().interacting());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.REFUSE_CHALLENGE, player.id(), other.id())
        );
    }

    @Test
    void successFromChallenger() {
        other.interactions().start(invitationHandler().invitation(other, player));

        RefuseChallenge action = new RefuseChallenge(player, other.id());

        action.start(new ActionQueue());

        assertFalse(player.interactions().interacting());
        assertFalse(other.interactions().interacting());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.REFUSE_CHALLENGE, player.id(), other.id())
        );
    }

    private ChallengeInvitationHandler invitationHandler() {
        return new ChallengeInvitationHandler(container.get(FightService.class).handler(ChallengeBuilder.class));
    }
}
