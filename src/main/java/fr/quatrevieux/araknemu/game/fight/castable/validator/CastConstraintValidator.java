package fr.quatrevieux.araknemu.game.fight.castable.validator;

import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Validate a single castable constraint
 */
public interface CastConstraintValidator<C extends Castable> {
    /**
     * Check if the spell can be casted
     *
     * @param turn The current turn
     * @param castable The action to cast
     * @param target The target cell
     *
     * @return The error if constraint failed, or null
     */
    public Error validate(FightTurn turn, C castable, FightCell target);
}
