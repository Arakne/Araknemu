package fr.quatrevieux.araknemu.game.exploration.interaction.action.challenge;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Action;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionQueue;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.challenge.ChallengeInvitation;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;
import fr.quatrevieux.araknemu.game.exploration.creature.Operation;

/**
 * Ask for challenge
 */
final public class AskChallenge implements Action {
    final private ExplorationPlayer player;
    final private int target;
    final private FightService fightService;

    public AskChallenge(ExplorationPlayer player, int target, FightService fightService) {
        this.player = player;
        this.target = target;
        this.fightService = fightService;
    }

    @Override
    public void start(ActionQueue queue) {
        player.map().creature(target).apply(new Operation() {
            @Override
            public void onExplorationPlayer(ExplorationPlayer challenger) {
                player
                    .interactions()
                    .start(new ChallengeInvitation(player, challenger, fightService.handler(ChallengeBuilder.class)))
                ;
            }
        });
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
        return new Object[] {target};
    }
}
