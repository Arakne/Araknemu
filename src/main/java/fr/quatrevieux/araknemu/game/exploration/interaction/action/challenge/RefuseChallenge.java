package fr.quatrevieux.araknemu.game.exploration.interaction.action.challenge;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Action;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionQueue;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.challenge.ChallengeRequestDialog;

/**
 * Refuse or cancel the challenge invitation
 */
final public class RefuseChallenge implements Action {
    final private ExplorationPlayer player;
    final private int target;

    public RefuseChallenge(ExplorationPlayer player, int target) {
        this.player = player;
        this.target = target;
    }

    @Override
    public void start(ActionQueue queue) {
        ChallengeRequestDialog dialog = player.interactions().get(ChallengeRequestDialog.class);

        if (dialog.initiator().id() != target) {
            throw new IllegalArgumentException("Invalid challenge target");
        }

        dialog.decline();
    }

    @Override
    public ExplorationPlayer performer() {
        return player;
    }

    @Override
    public ActionType type() {
        return ActionType.REFUSE_CHALLENGE;
    }

    @Override
    public Object[] arguments() {
        return new Object[] {target};
    }
}
