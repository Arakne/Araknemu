package fr.quatrevieux.araknemu.game.listener.player.inventory;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.inventory.event.KamasChanged;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;

/**
 * Send inventory kamas when changed
 */
final public class SendKamas implements Listener<KamasChanged> {
    final private GamePlayer player;

    public SendKamas(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(KamasChanged event) {
        player.send(new Stats(player.properties()));
    }

    @Override
    public Class<KamasChanged> event() {
        return KamasChanged.class;
    }
}
