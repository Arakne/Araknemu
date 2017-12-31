package fr.quatrevieux.araknemu.game.exploration;

import fr.quatrevieux.araknemu.game.event.exploration.StartExploration;
import fr.quatrevieux.araknemu.game.event.listener.player.InitializeGame;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Base service for handle game exploration
 */
final public class ExplorationService {
    /**
     * Start exploration for a player
     */
    public void start(GamePlayer player) {
        player.dispatcher().add(new InitializeGame(player));

        player.dispatch(new StartExploration());
    }
}
