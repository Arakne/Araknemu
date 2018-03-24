package fr.quatrevieux.araknemu.game.exploration.interaction.challenge;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChallengerDialogTest extends GameBaseCase {
    private ExplorationPlayer initiator;
    private ExplorationPlayer challenger;
    private ChallengeInvitation invitation;
    private ChallengerDialog dialog;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        initiator = explorationPlayer();
        challenger = new ExplorationPlayer(makeOtherPlayer());

        challenger.join(initiator.map());

        invitation = new ChallengeInvitation(initiator, challenger);
        initiator.interactions().start(invitation);

        dialog = new ChallengerDialog(invitation);
    }

    @Test
    void self() {
        assertSame(challenger, dialog.self());
    }

    @Test
    void interlocutor() {
        assertSame(initiator, dialog.interlocutor());
    }

    @Test
    void initiator() {
        assertSame(initiator, dialog.initiator());
    }

    @Test
    void stop() {
        dialog.stop();

        assertFalse(initiator.interactions().interacting());
        assertFalse(challenger.interactions().interacting());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.REFUSE_CHALLENGE, "" + challenger.id(), new Object[] {initiator.id()})
        );
    }

    @Test
    void decline() {
        dialog.decline();

        assertFalse(initiator.interactions().interacting());
        assertFalse(challenger.interactions().interacting());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.REFUSE_CHALLENGE, "" + challenger.id(), new Object[] {initiator.id()})
        );
    }

    @Test
    void accept() {
        dialog.accept();

        assertFalse(initiator.interactions().interacting());
        assertFalse(challenger.interactions().interacting());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.ACCEPT_CHALLENGE, "" + challenger.id(), new Object[] {initiator.id()})
        );
    }
}
