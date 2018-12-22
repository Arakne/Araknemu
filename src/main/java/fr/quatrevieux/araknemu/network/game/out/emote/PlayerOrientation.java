package fr.quatrevieux.araknemu.network.game.out.emote;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

/**
 * Send to client the player orientation
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Emotes.as#L116
 *
 * @todo Send for sprite or creature instead of player
 */
final public class PlayerOrientation {
    final private ExplorationPlayer player;

    public PlayerOrientation(ExplorationPlayer player) {
        this.player = player;
    }

    @Override
    public String toString() {
        return "eD" + player.id() + "|" + player.orientation().ordinal();
    }
}
