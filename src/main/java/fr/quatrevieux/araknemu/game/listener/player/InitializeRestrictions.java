package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.event.GameJoined;
import fr.quatrevieux.araknemu.network.game.out.account.AlterRestrictions;

/**
 * Send restrictions to client for initialization
 */
final public class InitializeRestrictions implements Listener<GameJoined> {
    final private GamePlayer player;

    public InitializeRestrictions(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(GameJoined event) {
        player.send(new AlterRestrictions(player.restrictions()));
    }

    @Override
    public Class<GameJoined> event() {
        return GameJoined.class;
    }
}
