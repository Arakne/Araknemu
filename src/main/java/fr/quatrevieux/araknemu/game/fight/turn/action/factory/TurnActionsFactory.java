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
final public class TurnActionsFactory {
    final private CastFactory castFactory;
    final private CloseCombatFactory closeCombatFactory;
    final private MoveFactory moveFactory;

    final private Map<ActionType, FightActionFactory> factories = new EnumMap<>(ActionType.class);

    public TurnActionsFactory(FightTurn turn) {
        this.moveFactory = new MoveFactory(turn);
        this.castFactory = new CastFactory(turn);
        this.closeCombatFactory = new CloseCombatFactory(turn);

        register(moveFactory);
        register(castFactory);
        register(closeCombatFactory);
    }

    public Action create(ActionType action, String[] arguments) {
        if (!factories.containsKey(action)) {
            throw new FightException("Fight action " + action + " not found");
        }

        return factories.get(action).create(arguments);
    }

    /**
     * Get the factory for spell cast action
     */
    public CastFactory cast() {
        return castFactory;
    }

    /**
     * Get the factory for close combat action
     */
    public CloseCombatFactory closeCombat() {
        return closeCombatFactory;
    }

    /**
     * Get the factory for move action
     */
    public MoveFactory move() {
        return moveFactory;
    }

    private void register(FightActionFactory factory) {
        factories.put(factory.type(), factory);
    }
}
