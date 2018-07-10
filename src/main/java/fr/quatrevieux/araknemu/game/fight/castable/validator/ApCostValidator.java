package fr.quatrevieux.araknemu.game.fight.castable.validator;

import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Validate spell AP cost
 */
final public class ApCostValidator implements CastConstraintValidator {
    @Override
    public Error validate(FightTurn turn, Castable castable, FightCell target) {
        return castable.apCost() > turn.points().actionPoints()
            ? Error.cantCastNotEnoughActionPoints(turn.points().actionPoints(), castable.apCost())
            : null
        ;
    }
}
