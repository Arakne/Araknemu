package fr.quatrevieux.araknemu.game.exploration.interaction.challenge;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

class InitiatorDialogTest extends GameBaseCase {
    private ExplorationPlayer initiator;
    private ExplorationPlayer challenger;
    private ChallengeInvitation invitation;
    private InitiatorDialog dialog;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        initiator = explorationPlayer();
        challenger = new ExplorationPlayer(makeOtherPlayer());

        initiator.join(container.get(ExplorationMapService.class).load(10340));
        challenger.join(initiator.map());

        invitation = new ChallengeInvitation(initiator, challenger, container.get(FightService.class).handler(ChallengeBuilder.class));
        initiator.interactions().start(invitation);

        dialog = new InitiatorDialog(invitation);
    }

    @Test
    void self() {
        assertSame(initiator, dialog.self());
    }

    @Test
    void interlocutor() {
        assertSame(challenger, dialog.interlocutor());
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
            new GameActionResponse("", ActionType.REFUSE_CHALLENGE, initiator.id(), challenger.id())
        );
    }

    @Test
    void decline() {
        dialog.decline();

        assertFalse(initiator.interactions().interacting());
        assertFalse(challenger.interactions().interacting());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.REFUSE_CHALLENGE, initiator.id(), challenger.id())
        );
    }
}
