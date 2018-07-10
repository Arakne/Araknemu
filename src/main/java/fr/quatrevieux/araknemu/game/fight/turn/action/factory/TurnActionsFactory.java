package fr.quatrevieux.araknemu.game.fight.turn.action.factory;

import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.CastFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.closeCombat.CloseCombatFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveFactory;

import java.util.EnumMap;
import java.util.Map;

/**
 * Action factory for all turn actions
 */
final public class TurnActionsFactory implements FightActionFactory {
    final private Map<ActionType, FightActionFactory> factories = new EnumMap<>(ActionType.class);

    public TurnActionsFactory(FightTurn turn) {
        factories.put(ActionType.MOVE, new MoveFactory(turn));
        factories.put(ActionType.CAST, new CastFactory(turn));
        factories.put(ActionType.CLOSE_COMBAT, new CloseCombatFactory(turn));
    }

    @Override
    public Action create(ActionType action, String[] arguments) throws Exception {
        if (!factories.containsKey(action)) {
            throw new FightException("Fight action " + action + " not found");
        }

        return factories.get(action).create(action, arguments);
    }
}
