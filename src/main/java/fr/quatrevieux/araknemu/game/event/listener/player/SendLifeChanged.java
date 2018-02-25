package fr.quatrevieux.araknemu.game.event.listener.player;

import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.common.LifeChanged;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.info.Information;

/**
 * Send packet after life changed
 */
final public class SendLifeChanged implements Listener<LifeChanged> {
    final private GamePlayer player;

    public SendLifeChanged(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(LifeChanged event) {
        if (event.diff() == 0) {
            return;
        }

        player.send(new Stats(player));

        if (event.diff() > 0) {
            player.send(Information.heal(event.diff()));
        }
    }

    @Override
    public Class<LifeChanged> event() {
        return LifeChanged.class;
    }
}
