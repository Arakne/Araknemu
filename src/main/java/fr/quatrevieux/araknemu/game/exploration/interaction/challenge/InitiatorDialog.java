package fr.quatrevieux.araknemu.game.exploration.interaction.challenge;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;

/**
 * Dialog for challenge initiator
 */
final public class InitiatorDialog extends ChallengeDialog {
    public InitiatorDialog(ChallengeInvitation invitation) {
        super(invitation);
    }

    @Override
    public ExplorationPlayer self() {
        return invitation.initiator();
    }

    @Override
    public ExplorationPlayer interlocutor() {
        return invitation.challenger();
    }

    @Override
    public Interaction start() {
        self().map().send(
            new GameActionResponse(
                "",
                ActionType.CHALLENGE,
                Integer.toString(self().id()),
                new Object[] {interlocutor().id()}
            )
        );

        return this;
    }
}
