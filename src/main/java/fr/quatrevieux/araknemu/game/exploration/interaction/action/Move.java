package fr.quatrevieux.araknemu.game.exploration.interaction.action;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.event.PlayerMoving;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.world.map.path.Path;
import fr.quatrevieux.araknemu.game.world.map.path.PathStep;

/**
 * Move the player
 */
final public class Move implements BlockingAction {
    final private ExplorationPlayer player;
    private Path<ExplorationMapCell> path;

    private int id;

    public Move(ExplorationPlayer player, Path<ExplorationMapCell> path) {
        this.player = player;
        this.path = path;
    }

    @Override
    public void start() {
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Empty path");
        }

        player.map().dispatch(new PlayerMoving(player, this));
    }

    @Override
    public void cancel(String argument) {
        if (argument == null) {
            return;
        }

        int cellId = Integer.parseInt(argument);

        for (PathStep<ExplorationMapCell> step : path) {
            if (step.cell().id() == cellId) {
                player.move(step.cell());

                return;
            }
        }

        throw new IllegalArgumentException("Invalid cell");
    }

    @Override
    public void end() {
        player.move(path.target());
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

    /**
     * Replace the move path
     */
    public void replace(Path<ExplorationMapCell> path) {
        this.path = path;
    }
}
