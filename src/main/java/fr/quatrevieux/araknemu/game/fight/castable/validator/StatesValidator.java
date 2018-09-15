package fr.quatrevieux.araknemu.game.fight.castable.validator;

import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Validate fighter states
 */
final public class StatesValidator implements CastConstraintValidator {
    @Override
    public Error validate(FightTurn turn, Castable castable, FightCell target) {
        if (
            !turn.fighter().states().hasAll(castable.constraints().requiredStates())
            || turn.fighter().states().hasOne(castable.constraints().forbiddenStates())
        ) {
            return Error.cantCastBadState();
        }

        return null;
    }
}
