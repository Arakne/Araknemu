package fr.quatrevieux.araknemu.game.fight.turn.action.move.validators;

import fr.arakne.utils.maps.path.Path;

import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator.PathValidationException;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;

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
    public Path<FightCell> validate(Move move, Path<FightCell> path) throws PathValidationException;
}
