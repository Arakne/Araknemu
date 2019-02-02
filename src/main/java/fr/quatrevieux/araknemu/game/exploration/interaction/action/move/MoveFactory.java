package fr.quatrevieux.araknemu.game.exploration.interaction.action.move;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Action;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.SingleActionFactory;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator.PathValidator;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.world.map.path.Decoder;

/**
 * Create the exploration move action
 */
final public class MoveFactory implements SingleActionFactory {
    final private PathValidator[] validators;

    public MoveFactory(PathValidator... validators) {
        this.validators = validators;
    }

    @Override
    public ActionType type() {
        return ActionType.MOVE;
    }

    @Override
    public Action create(ExplorationPlayer player, ActionType action, String[] arguments) {
        ExplorationMap map = player.map();

        return new Move(
            player,
            new Decoder<>(map).decode(arguments[0], map.get(player.position().cell())),
            validators
        );
    }
}
