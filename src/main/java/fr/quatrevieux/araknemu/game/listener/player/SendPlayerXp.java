package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.experience.event.PlayerXpChanged;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;

/**
 * Send to client new player xp
 */
final public class SendPlayerXp implements Listener<PlayerXpChanged> {
    final private GamePlayer player;

    public SendPlayerXp(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(PlayerXpChanged event) {
        player.send(new Stats(player.scope().properties()));
    }

    @Override
    public Class<PlayerXpChanged> event() {
        return PlayerXpChanged.class;
    }
}
