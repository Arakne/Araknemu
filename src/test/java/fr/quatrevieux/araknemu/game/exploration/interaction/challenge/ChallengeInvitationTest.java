package fr.quatrevieux.araknemu.game.exploration.interaction.challenge;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeInvitationTest extends GameBaseCase {
    private ExplorationPlayer initiator;
    private ExplorationPlayer challenger;
    private ChallengeInvitation invitation;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        initiator = explorationPlayer();
        challenger = new ExplorationPlayer(makeOtherPlayer());

        challenger.join(initiator.map());

        invitation = new ChallengeInvitation(initiator, challenger);
    }

    @Test
    void getters() {
        assertSame(initiator, invitation.initiator());
        assertSame(challenger, invitation.challenger());

        assertCollectionEquals(invitation.interlocutors(), initiator, challenger);
    }

    @Test
    void startInitiatorBusy() {
        Interaction interaction = Mockito.mock(Interaction.class);
        Mockito.when(interaction.start()).thenReturn(interaction);

        initiator.interactions().start(interaction);

        assertNull(invitation.start());
        assertFalse(challenger.interactions().interacting());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.JOIN_FIGHT_ERROR, "" + initiator.id(), new Object[] {"o"})
        );
    }

    @Test
    void startChallengerBusy() {
        Interaction interaction = Mockito.mock(Interaction.class);
        Mockito.when(interaction.start()).thenReturn(interaction);

        challenger.interactions().start(interaction);

        assertNull(invitation.start());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.JOIN_FIGHT_ERROR, "" + initiator.id(), new Object[] {"z"})
        );
    }

    @Test
    void startNotOneSameMap() throws ContainerException {
        challenger.join(
            container.get(ExplorationMapService.class).load(10540)
        );

        assertNull(invitation.start());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.JOIN_FIGHT_ERROR, "" + initiator.id(), new Object[] {"p"})
        );
    }

    @Test
    void startSuccess() {
        Interaction interaction = invitation.start();

        assertInstanceOf(InitiatorDialog.class, interaction);
        assertTrue(challenger.interactions().busy());
        assertInstanceOf(ChallengerDialog.class, challenger.interactions().get(Interaction.class));

        requestStack.assertLast(
            new GameActionResponse("", ActionType.CHALLENGE, "" + initiator.id(), new Object[] {challenger.id()})
        );
    }

    @Test
    void stop() {
        initiator.interactions().start(invitation);
        invitation.stop();

        assertFalse(initiator.interactions().interacting());
        assertFalse(challenger.interactions().interacting());
    }
}
