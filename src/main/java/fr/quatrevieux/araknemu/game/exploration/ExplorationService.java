package fr.quatrevieux.araknemu.game.exploration;

import fr.quatrevieux.araknemu.game.event.exploration.StartExploration;
import fr.quatrevieux.araknemu.game.event.listener.player.InitializeGame;
import fr.quatrevieux.araknemu.game.event.listener.player.SendMapData;
import fr.quatrevieux.araknemu.game.event.listener.player.SendNewSprite;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Base service for handle game exploration
 */
final public class ExplorationService {
    final private ExplorationMapService mapService;

    public ExplorationService(ExplorationMapService mapService) {
        this.mapService = mapService;
    }

    /**
     * Start exploration for a player
     */
    public void start(GamePlayer player) {
        player.dispatcher().add(new InitializeGame(player, mapService));
        player.dispatcher().add(new SendMapData(player));
        player.dispatcher().add(new SendNewSprite(player));

        player.dispatch(new StartExploration());
    }
}
