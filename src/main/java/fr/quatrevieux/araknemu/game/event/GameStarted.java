package fr.quatrevieux.araknemu.game.event;

import fr.quatrevieux.araknemu.game.GameService;

/**
 * The game service is started
 */
final public class GameStarted {
    final private GameService service;

    public GameStarted(GameService service) {
        this.service = service;
    }

    public GameService service() {
        return service;
    }
}
