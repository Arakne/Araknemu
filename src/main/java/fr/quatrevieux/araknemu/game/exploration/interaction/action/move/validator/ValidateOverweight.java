package fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.Move;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.world.map.path.Path;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Deny movement if the player is overweight
 */
final public class ValidateOverweight implements PathValidator {
    @Override
    public Path<ExplorationMapCell> validate(Move move, Path<ExplorationMapCell> path) throws PathValidationException {
        final ExplorationPlayer player = move.performer();

        if (player.player().inventory().overweight()) {
            throw new PathValidationException(Error.cantMoveOverweight());
        }

        return path;
    }
}
