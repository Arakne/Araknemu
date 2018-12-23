package fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator;

import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.Move;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.game.world.map.path.Path;
import fr.quatrevieux.araknemu.game.world.map.path.PathStep;

/**
 * Validate restricted directions if the player is not allowed
 */
final public class ValidateRestrictedDirections implements PathValidator {
    @Override
    public Path<ExplorationMapCell> validate(Move move, Path<ExplorationMapCell> path) {
        if (move.performer().player().restrictions().canMoveAllDirections()) {
            return path;
        }

        // Validate directions expect the first one : its value is always "a"
        if (path.stream().skip(1).map(PathStep::direction).allMatch(Direction::restricted)) {
            return path;
        }

        throw new IllegalArgumentException("Invalid direction detected");
    }
}
