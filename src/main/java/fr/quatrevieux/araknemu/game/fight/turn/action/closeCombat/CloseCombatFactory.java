package fr.quatrevieux.araknemu.game.fight.turn.action.closeCombat;

import fr.quatrevieux.araknemu.game.fight.castable.weapon.WeaponConstraintsValidator;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.FightActionFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.BaseCriticalityStrategy;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.CriticalityStrategy;

/**
 * Factory for close combat action
 */
final public class CloseCombatFactory implements FightActionFactory {
    final private FightTurn turn;
    final private WeaponConstraintsValidator validator;
    final private CriticalityStrategy criticalityStrategy;

    public CloseCombatFactory(FightTurn turn) {
        this(turn, new WeaponConstraintsValidator(turn), new BaseCriticalityStrategy(turn.fighter()));
    }

    public CloseCombatFactory(FightTurn turn, WeaponConstraintsValidator validator, CriticalityStrategy criticalityStrategy) {
        this.turn = turn;
        this.validator = validator;
        this.criticalityStrategy = criticalityStrategy;
    }

    @Override
    public Action create(ActionType action, String[] arguments) {
        return new CloseCombat(
            turn,
            turn.fighter(),
            turn.fight().map().get(Integer.parseInt(arguments[0])),
            validator,
            criticalityStrategy
        );
    }
}
