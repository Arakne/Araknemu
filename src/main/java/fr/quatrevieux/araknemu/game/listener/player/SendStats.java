package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.characteristic.event.CharacteristicsChanged;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;

/**
 * Send stats when characteristics are changed
 */
final public class SendStats implements Listener<CharacteristicsChanged> {
    final private GamePlayer player;

    public SendStats(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(CharacteristicsChanged event) {
        player.send(new Stats(player.scope().properties()));
    }

    @Override
    public Class<CharacteristicsChanged> event() {
        return CharacteristicsChanged.class;
    }
}
