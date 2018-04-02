package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.experience.event.PlayerLevelUp;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.account.NewPlayerLevel;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;

/**
 * Send packets after player level up
 */
final public class SendLevelUp implements Listener<PlayerLevelUp> {
    final private GamePlayer player;

    public SendLevelUp(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(PlayerLevelUp event) {
        player.send(new NewPlayerLevel(event.level()));
        player.send(new Stats(player));
    }

    @Override
    public Class<PlayerLevelUp> event() {
        return PlayerLevelUp.class;
    }
}
