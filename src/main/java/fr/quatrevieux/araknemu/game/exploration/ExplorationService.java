package fr.quatrevieux.araknemu.game.exploration;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.game.exploration.event.ExplorationPlayerCreated;
import fr.quatrevieux.araknemu.game.listener.player.StopExploration;
import fr.quatrevieux.araknemu.game.listener.player.InitializeGame;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Base service for handle game exploration
 */
final public class ExplorationService {
    final private ExplorationMapService mapService;
    final private Dispatcher dispatcher;

    public ExplorationService(ExplorationMapService mapService, Dispatcher dispatcher) {
        this.mapService = mapService;
        this.dispatcher = dispatcher;
    }

    /**
     * Start exploration for a player
     */
    public ExplorationPlayer create(GamePlayer player) {
        ExplorationPlayer exploration = new ExplorationPlayer(player);

        exploration.dispatcher().add(new InitializeGame(exploration, mapService));
        exploration.dispatcher().add(new StopExploration(exploration));

        dispatcher.dispatch(new ExplorationPlayerCreated(exploration));

        return exploration;
    }
}
