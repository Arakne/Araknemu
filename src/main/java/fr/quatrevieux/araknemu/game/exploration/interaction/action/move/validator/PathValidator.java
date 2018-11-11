package fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator;

import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.Move;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.world.map.path.Path;

/**
 * Validate a move path
 */
public interface PathValidator {
    /**
     * Validate the path and return the filtered path
     *
     * @param move Move action
     * @param path Path to validate
     *
     * @return The filtered path
     */
    public Path<ExplorationMapCell> validate(Move move, Path<ExplorationMapCell> path);
}
