package fr.quatrevieux.araknemu.game.exploration.interaction.action;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;

/**
 * Launch a firework to the map
 */
final public class LaunchFirework implements BlockingAction {
    final private ExplorationPlayer player;

    final private int cell;
    final private int animation;
    final private int size;

    private int id;

    public LaunchFirework(ExplorationPlayer player, int cell, int animation, int size) {
        this.player = player;
        this.cell = cell;
        this.animation = animation;
        this.size = size;
    }

    @Override
    public void start() throws Exception {
        player.map().send(new GameActionResponse(this));
    }

    @Override
    public void cancel(String argument) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void end() throws Exception {}

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
        return ActionType.FIREWORK;
    }

    @Override
    public Object[] arguments() {
        return new Object[] {
            cell + "," + animation + ",11,8," + size
        };
    }
}
