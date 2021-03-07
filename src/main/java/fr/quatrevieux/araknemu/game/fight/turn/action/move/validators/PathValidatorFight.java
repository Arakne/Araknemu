package fr.quatrevieux.araknemu.game.fight.turn.action.move.validators;

import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveResult;

/**
 * Validate a move path
 */
public interface PathValidatorFight {

    /**
     * Validate the path and return the MoveResult
     *
     * @param move Move action
     * @param result MoveResult the result of the previous validator
     *
     * @return The filtered path
     */
    public MoveResult validate(Move move, MoveResult result);
}
