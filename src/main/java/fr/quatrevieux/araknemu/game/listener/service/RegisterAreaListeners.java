package fr.quatrevieux.araknemu.game.listener.service;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;
import fr.quatrevieux.araknemu.game.listener.player.InitializeAreas;
import fr.quatrevieux.araknemu.game.exploration.area.AreaService;

/**
 * Register areas listeners when player is loaded
 */
final public class RegisterAreaListeners implements Listener<PlayerLoaded> {
    final private AreaService service;

    public RegisterAreaListeners(AreaService service) {
        this.service = service;
    }

    @Override
    public void on(PlayerLoaded event) {
        event.player().dispatcher().add(new InitializeAreas(event.player(), service));
    }

    @Override
    public Class<PlayerLoaded> event() {
        return PlayerLoaded.class;
    }
}
