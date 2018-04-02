package fr.quatrevieux.araknemu.game.listener.service;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;
import fr.quatrevieux.araknemu.game.listener.player.fight.AttachFighter;

/**
 * Register listeners for the fight
 */
final public class AddFightListenersForPlayer implements Listener<PlayerLoaded> {
    @Override
    public void on(PlayerLoaded event) {
        event.player().dispatcher().add(new AttachFighter(event.player()));
    }

    @Override
    public Class<PlayerLoaded> event() {
        return PlayerLoaded.class;
    }
}
