package fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator;

import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.Move;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.world.map.path.Path;

/**
 * Validate move path : stop the player when a non-walkable cell is found
 */
final public class ValidateWalkable implements PathValidator {
    @Override
    public Path<ExplorationMapCell> validate(Move move, Path<ExplorationMapCell> path) {
        return path.keepWhile(step -> step.cell().walkable());
    }
}
