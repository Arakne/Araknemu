package fr.quatrevieux.araknemu.game.fight.castable.validator;

import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Aggregates of constraints
 */
final public class ConstraintsAggregateValidator<T extends Castable> implements CastConstraintValidator<T> {
    final private CastConstraintValidator<? super T>[] validators;

    public ConstraintsAggregateValidator(CastConstraintValidator<? super T>[] validators) {
        this.validators = validators;
    }

    @Override
    public Error validate(FightTurn turn, T action, FightCell target) {
        for (CastConstraintValidator<? super T> validator : validators) {
            Error error = validator.validate(turn, action, target);

            if (error != null) {
                return error;
            }
        }

        return null;
    }
}
