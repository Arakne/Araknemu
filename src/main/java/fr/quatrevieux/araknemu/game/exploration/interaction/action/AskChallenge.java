package fr.quatrevieux.araknemu.game.exploration.interaction.action;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.challenge.ChallengeInvitation;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;

/**
 * Ask for challenge
 */
final public class AskChallenge implements Action {
    final private ExplorationPlayer player;
    final private ExplorationPlayer challenger;
    final private FightService fightService;

    public AskChallenge(ExplorationPlayer player, ExplorationPlayer challenger, FightService fightService) {
        this.player = player;
        this.challenger = challenger;
        this.fightService = fightService;
    }

    @Override
    public void start() {
        player
            .interactions()
            .start(new ChallengeInvitation(player, challenger, fightService.handler(ChallengeBuilder.class)))
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
