package fr.quatrevieux.araknemu.game.listener.service;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.listener.player.SendPlayerXp;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;
import fr.quatrevieux.araknemu.game.listener.player.SendLevelUp;
import fr.quatrevieux.araknemu.game.listener.player.SendPlayerXp;

/**
 * Add listeners for player levels
 */
final public class AddLevelListeners implements Listener<PlayerLoaded> {
    @Override
    public void on(PlayerLoaded event) {
        event.player().dispatcher().add(new SendLevelUp(event.player()));
        event.player().dispatcher().add(new SendPlayerXp(event.player()));
    }

    @Override
    public Class<PlayerLoaded> event() {
        return PlayerLoaded.class;
    }
}
