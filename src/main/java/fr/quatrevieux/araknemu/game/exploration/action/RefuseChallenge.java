package fr.quatrevieux.araknemu.game.exploration.action;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.challenge.ChallengeDialog;

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
    public void start() {
        ChallengeDialog dialog = player.interactions().get(ChallengeDialog.class);

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
