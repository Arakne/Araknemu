package fr.quatrevieux.araknemu.game.exploration.interaction.challenge;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.ExplorationService;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.network.game.out.fight.JoinFight;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.NOPLogger;

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
        challenger = makeOtherExplorationPlayer();

        initiator.join(container.get(ExplorationMapService.class).load(10340));
        challenger.join(initiator.map());

        invitation = new ChallengeInvitation(initiator, challenger, container.get(FightService.class).handler(ChallengeBuilder.class));
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
            new GameActionResponse("", ActionType.REFUSE_CHALLENGE, challenger.id(), initiator.id())
        );
    }

    @Test
    void decline() {
        dialog.decline();

        assertFalse(initiator.interactions().interacting());
        assertFalse(challenger.interactions().interacting());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.REFUSE_CHALLENGE, challenger.id(), initiator.id())
        );
    }

    @Test
    void accept() {
        dialog.accept();

        assertFalse(initiator.interactions().interacting());
        assertFalse(challenger.interactions().interacting());

        assertTrue(initiator.player().isFighting());
        assertFalse(initiator.player().isExploring());
        assertTrue(challenger.player().isFighting());
        assertFalse(challenger.player().isExploring());

        assertFalse(initiator.map().players().contains(initiator));
        assertFalse(initiator.map().players().contains(challenger));

        assertInstanceOf(ChallengeType.class, initiator.player().fighter().fight().type());
        assertInstanceOf(PlacementState.class, initiator.player().fighter().fight().state());

        requestStack.assertOne(new GameActionResponse("", ActionType.ACCEPT_CHALLENGE, challenger.id(), initiator.id()));
        requestStack.assertOne(new JoinFight(session.fighter().fight()));
    }
}
