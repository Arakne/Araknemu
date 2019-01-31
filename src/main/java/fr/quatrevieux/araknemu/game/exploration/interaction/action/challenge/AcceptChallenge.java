package fr.quatrevieux.araknemu.game.exploration.interaction.action.challenge;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Action;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionQueue;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.challenge.ChallengerDialog;

/**
 * Accept the challenge invitation
 */
final public class AcceptChallenge implements Action {
    final private ExplorationPlayer player;
    final private int target;

    public AcceptChallenge(ExplorationPlayer player, int target) {
        this.player = player;
        this.target = target;
    }

    @Override
    public void start(ActionQueue queue) {
        ChallengerDialog dialog = player.interactions().get(ChallengerDialog.class);

        if (dialog.initiator().id() != target) {
            throw new IllegalArgumentException("Invalid challenge target");
        }

        dialog.accept();
    }

    @Override
    public ExplorationPlayer performer() {
        return player;
    }

    @Override
    public ActionType type() {
        return ActionType.ACCEPT_CHALLENGE;
    }

    @Override
    public Object[] arguments() {
        return new Object[] {target};
    }
}
