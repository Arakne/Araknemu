package fr.quatrevieux.araknemu.game.exploration.interaction.action.move;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionQueue;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.BlockingAction;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;

import java.util.Arrays;

/**
 * Change current map after a move
 */
final public class ChangeMap implements BlockingAction {
    final private ExplorationPlayer player;
    final private ExplorationMap map;
    final private int cell;
    final private int cinematic;

    private int id;

    public ChangeMap(ExplorationPlayer player, ExplorationMap map, int cell, int cinematic) {
        this.player = player;
        this.map = map;
        this.cell = cell;
        this.cinematic = cinematic;
    }

    public ChangeMap(ExplorationPlayer player, ExplorationMap map, int cell) {
        this(player, map, cell, 0);
    }

    @Override
    public void start(ActionQueue queue) {
        queue.setPending(this);

        player.leave();

        player.send(new GameActionResponse(this));
    }

    @Override
    public void cancel(String argument) {}

    @Override
    public void end() {
        player.changeMap(map, cell);
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
        return ActionType.CHANGE_MAP;
    }

    @Override
    public Object[] arguments() {
        return new Object[] {cinematic != 0 ? cinematic : ""};
    }
}
