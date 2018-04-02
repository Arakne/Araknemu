package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.handler.event.Disconnected;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Save the player on disconnect
 */
final public class SavePlayer implements Listener<Disconnected> {
    final private GamePlayer player;

    public SavePlayer(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(Disconnected event) {
        player.save();
    }

    @Override
    public Class<Disconnected> event() {
        return Disconnected.class;
    }
}
