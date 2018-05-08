package fr.quatrevieux.araknemu.game.fight.turn.action.factory;

import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.Cast;
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

        factories.put(
            ActionType.CAST,
            (action, arguments) -> {
                Fighter fighter = turn.fighter();
                int spellId = Integer.parseInt(arguments[0]);

                return new Cast(
                    turn,
                    fighter,
                    fighter.spells().has(spellId) ? fighter.spells().get(spellId) : null,
                    turn.fight().map().get(Integer.parseInt(arguments[1]))
                );
            }
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
