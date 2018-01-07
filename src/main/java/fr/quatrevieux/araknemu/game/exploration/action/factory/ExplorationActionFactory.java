package fr.quatrevieux.araknemu.game.exploration.action.factory;

import fr.quatrevieux.araknemu.game.exploration.action.Action;
import fr.quatrevieux.araknemu.game.exploration.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.action.Move;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionRequest;

import java.util.EnumMap;

/**
 * Factory for all exploration game actions
 */
final public class ExplorationActionFactory implements ActionFactory {
    final private EnumMap<ActionType, ActionFactory> factories = new EnumMap<>(ActionType.class);

    public ExplorationActionFactory() {
        factories.put(
            ActionType.MOVE,
            (player, request) -> new Move(
                player.actionQueue().generateId(),
                player,
                player.map().decoder().decodePath(
                    request.arguments()[0],
                    player.position().cell()
                )
            )
        );
    }

    @Override
    public Action create(GamePlayer player, GameActionRequest request) throws Exception {
        if (!factories.containsKey(request.type())) {
            throw new Exception("No factory found for game action : " + request.type());
        }

        return factories.get(request.type()).create(player, request);
    }
}
