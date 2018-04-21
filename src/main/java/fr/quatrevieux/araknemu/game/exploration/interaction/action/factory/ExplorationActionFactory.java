package fr.quatrevieux.araknemu.game.exploration.interaction.action.factory;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.*;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.world.map.path.Decoder;

import java.util.EnumMap;

/**
 * Factory for all exploration game actions
 */
final public class ExplorationActionFactory implements ActionFactory {
    final private EnumMap<ActionType, ActionFactory> factories = new EnumMap<>(ActionType.class);

    public ExplorationActionFactory(FightService fightService) {
        factories.put(
            ActionType.MOVE,
            (player, action, arguments) -> new Move(
                player,
                new Decoder<>(player.map()).decode(arguments[0], player.map().get(player.position().cell()))
            )
        );

        factories.put(
            ActionType.CHALLENGE,
            (player, action, arguments) -> new AskChallenge(
                player,
                player.map().getPlayer(Integer.parseInt(arguments[0])),
                fightService
            )
        );

        factories.put(
            ActionType.ACCEPT_CHALLENGE,
            (player, action, arguments) -> new AcceptChallenge(player, Integer.parseInt(arguments[0]))
        );

        factories.put(
            ActionType.REFUSE_CHALLENGE,
            (player, action, arguments) -> new RefuseChallenge(player, Integer.parseInt(arguments[0]))
        );
    }

    @Override
    public Action create(ExplorationPlayer player, ActionType action, String[] arguments) throws Exception {
        if (!factories.containsKey(action)) {
            throw new Exception("No factory found for game action : " + action);
        }

        return factories.get(action).create(player, action, arguments);
    }
}
