package fr.quatrevieux.araknemu.game.exploration.action;

import fr.quatrevieux.araknemu.game.event.exploration.action.PlayerMoving;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.world.map.PathStep;

import java.util.List;

/**
 * Move the player
 */
final public class Move implements Action {
    final private int id;
    final private GamePlayer player;
    private List<PathStep> path;

    public Move(int id, GamePlayer player, List<PathStep> path) {
        this.id = id;
        this.player = player;
        this.path = path;
    }

    @Override
    public void start() throws Exception {
        if (path.isEmpty()) {
            throw new Exception("Empty path");
        }

        player.map().dispatch(new PlayerMoving(player, this));
    }

    @Override
    public void cancel(String argument) throws Exception {
        int cellId = Integer.parseInt(argument);

        for (PathStep step : path) {
            if (step.cell() == cellId) {
                player.goTo(
                    player.position().newCell(cellId)
                );

                return;
            }
        }

        throw new Exception("Invalid cell");
    }

    @Override
    public void end() {
        player.goTo(
            player.position().newCell(
                path.get(path.size() - 1).cell()
            )
        );
    }

    @Override
    public int id() {
        return id;
    }

    public List<PathStep> path() {
        return path;
    }

    /**
     * Replace the move path
     */
    public void replace(List<PathStep> path) {
        this.path = path;
    }
}
