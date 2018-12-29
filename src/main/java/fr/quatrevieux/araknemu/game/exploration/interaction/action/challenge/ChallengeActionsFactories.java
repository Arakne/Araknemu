package fr.quatrevieux.araknemu.game.exploration.interaction.action.challenge;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ExplorationActionRegistry;
import fr.quatrevieux.araknemu.game.fight.FightService;

/**
 * Register challenge actions
 */
final public class ChallengeActionsFactories implements ExplorationActionRegistry.SelfRegisterable {
    final private FightService fightService;

    public ChallengeActionsFactories(FightService fightService) {
        this.fightService = fightService;
    }

    @Override
    public void register(ExplorationActionRegistry factory) {
        factory.register(ActionType.CHALLENGE, this::ask);
        factory.register(ActionType.ACCEPT_CHALLENGE, this::accept);
        factory.register(ActionType.REFUSE_CHALLENGE, this::refuse);
    }

    private AskChallenge ask(ExplorationPlayer player, ActionType action, String[] arguments) {
        return new AskChallenge(player, Integer.parseInt(arguments[0]), fightService);
    }

    private AcceptChallenge accept(ExplorationPlayer player, ActionType action, String[] arguments) {
        return new AcceptChallenge(player, Integer.parseInt(arguments[0]));
    }

    private RefuseChallenge refuse(ExplorationPlayer player, ActionType action, String[] arguments) {
        return new RefuseChallenge(player, Integer.parseInt(arguments[0]));
    }
}
