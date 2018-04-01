package fr.quatrevieux.araknemu.game.exploration.interaction.action.factory;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.*;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionRequest;

import java.util.EnumMap;

/**
 * Factory for all exploration game actions
 */
final public class ExplorationActionFactory implements ActionFactory {
    final private EnumMap<ActionType, ActionFactory> factories = new EnumMap<>(ActionType.class);

    public ExplorationActionFactory(FightService fightService) {
        factories.put(
            ActionType.MOVE,
            (player, request) -> new Move(
                player,
                player.map().decoder().decodePath(
                    request.arguments()[0],
                    player.position().cell()
                )
            )
        );

        factories.put(
            ActionType.CHALLENGE,
            (player, request) -> new AskChallenge(
                player,
                player.map().getPlayer(Integer.parseInt(request.arguments()[0])),
                fightService
            )
        );

        factories.put(
            ActionType.ACCEPT_CHALLENGE,
            (player, request) -> new AcceptChallenge(player, Integer.parseInt(request.arguments()[0]))
        );

        factories.put(
            ActionType.REFUSE_CHALLENGE,
            (player, request) -> new RefuseChallenge(player, Integer.parseInt(request.arguments()[0]))
        );
    }

    @Override
    public Action create(ExplorationPlayer player, GameActionRequest request) throws Exception {
        if (!factories.containsKey(request.type())) {
            throw new Exception("No factory found for game action : " + request.type());
        }

        return factories.get(request.type()).create(player, request);
    }
}
