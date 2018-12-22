package fr.quatrevieux.araknemu.game.exploration.interaction.action.move;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionQueue;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.BlockingAction;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator.PathValidator;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.world.map.path.Path;
import fr.quatrevieux.araknemu.game.world.map.path.PathStep;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;

/**
 * Move the player
 */
final public class Move implements BlockingAction {
    final private ExplorationPlayer player;
    final private PathValidator[] validators;
    private Path<ExplorationMapCell> path;

    private int id;

    public Move(ExplorationPlayer player, Path<ExplorationMapCell> path, PathValidator[] validators) {
        this.player = player;
        this.path = path;
        this.validators = validators;
    }

    @Override
    public void start(ActionQueue queue) {
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Empty path");
        }

        for (PathValidator validator : validators) {
            path = validator.validate(this, path);
        }

        if (path.target().id() != player.cell()) {
            queue.setPending(this);
            player.map().send(new GameActionResponse(this));
        }
    }

    @Override
    public void cancel(String argument) {
        if (argument == null) {
            return;
        }

        int cellId = Integer.parseInt(argument);

        for (PathStep<ExplorationMapCell> step : path) {
            if (step.cell().id() == cellId) {
                player.move(step.cell(), step.direction());

                return;
            }
        }

        throw new IllegalArgumentException("Invalid cell");
    }

    @Override
    public void end() {
        player.move(path.target(), path.last().direction());
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public ExplorationPlayer performer() {
        return player;
    }

    @Override
    public ActionType type() {
        return ActionType.MOVE;
    }

    @Override
    public Object[] arguments() {
        return new Object[] { path.encode() };
    }

    public Path<ExplorationMapCell> path() {
        return path;
    }
}
