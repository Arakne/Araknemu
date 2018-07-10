package fr.quatrevieux.araknemu.game.fight.castable.validator;

import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Validate the target cell
 */
final public class TargetCellValidator implements CastConstraintValidator {
    @Override
    public Error validate(FightTurn turn, Castable castable, FightCell target) {
        if (!target.walkableIgnoreFighter()) {
            return Error.cantCastCellNotAvailable();
        }

        if (castable.constraints().freeCell() && target.fighter().isPresent()) {
            return Error.cantCastInvalidCell();
        }

        return null;
    }
}
