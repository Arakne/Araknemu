package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.event.RestrictionsChanged;
import fr.quatrevieux.araknemu.network.game.out.account.AlterRestrictions;

/**
 * Send restrictions to client when changed
 */
final public class SendRestrictions implements Listener<RestrictionsChanged> {
    final private GamePlayer player;

    public SendRestrictions(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(RestrictionsChanged event) {
        player.send(new AlterRestrictions(event.restrictions()));
    }

    @Override
    public Class<RestrictionsChanged> event() {
        return RestrictionsChanged.class;
    }
}
