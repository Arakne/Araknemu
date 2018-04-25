package fr.quatrevieux.araknemu.game.fight.turn.action.factory;

import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.quatrevieux.araknemu.game.world.map.path.Decoder;

import java.util.EnumMap;
import java.util.Map;

/**
 * Action factory for all turn actions
 */
final public class TurnActionsFactory implements FightActionFactory {
    final private Map<ActionType, FightActionFactory> factories = new EnumMap<>(ActionType.class);

    public TurnActionsFactory(FightTurn turn) {
        factories.put(
            ActionType.MOVE,
            (action, arguments) -> new Move(
                turn,
                turn.fighter(),
                new Decoder<>(turn.fight().map()).decode(
                    arguments[0],
                    turn.fighter().cell()
                )
            )
        );
    }

    @Override
    public Action create(ActionType action, String[] arguments) throws Exception {
        if (!factories.containsKey(action)) {
            throw new FightException("Fight action " + action + " not found");
        }

        return factories.get(action).create(action, arguments);
    }
}
