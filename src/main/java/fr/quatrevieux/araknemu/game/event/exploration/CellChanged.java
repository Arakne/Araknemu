package fr.quatrevieux.araknemu.game.event.exploration;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

/**
 * Trigger when player change cell.
 * Dispatch to map
 */
final public class CellChanged {
    final private ExplorationPlayer player;
    final private int cell;

    public CellChanged(ExplorationPlayer player, int cell) {
        this.player = player;
        this.cell = cell;
    }

    public ExplorationPlayer player() {
        return player;
    }

    public int cell() {
        return cell;
    }
}
