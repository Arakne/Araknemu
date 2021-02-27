package fr.quatrevieux.araknemu.game.fight.turn.action.move.validators;

import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveResult;

/**
 * Validate a move path
 */
public interface PathValidatorFight {
    /**
     * Validate the path and return the filtered path
     *
     * @param move Move action
     * @param path Path to validate
     *
     * @return The filtered path
     */
    public MoveResult validate(Move move, MoveResult result);
}
