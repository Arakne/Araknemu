package fr.quatrevieux.araknemu.game.exploration.interaction.event;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;

/**
 * Event trigger when the player move is terminated
 */
final public class PlayerMoveFinished {
    final private ExplorationPlayer player;
    final private ExplorationMapCell cell;

    public PlayerMoveFinished(ExplorationPlayer player, ExplorationMapCell cell) {
        this.player = player;
        this.cell = cell;
    }

    public ExplorationPlayer player() {
        return player;
    }

    public ExplorationMapCell cell() {
        return cell;
    }
}
