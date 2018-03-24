package fr.quatrevieux.araknemu.game.exploration.action;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.challenge.ChallengeInvitation;

/**
 * Ask for challenge
 */
final public class AskChallenge implements Action {
    final private ExplorationPlayer player;
    final private ExplorationPlayer challenger;

    public AskChallenge(ExplorationPlayer player, ExplorationPlayer challenger) {
        this.player = player;
        this.challenger = challenger;
    }

    @Override
    public void start() {
        player
            .interactions()
            .start(new ChallengeInvitation(player, challenger))
        ;
    }

    @Override
    public ExplorationPlayer performer() {
        return player;
    }

    @Override
    public ActionType type() {
        return ActionType.CHALLENGE;
    }

    @Override
    public Object[] arguments() {
        return new Object[] {challenger.id()};
    }
}
