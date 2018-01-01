package fr.quatrevieux.araknemu.game.event.listener.player;

import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.exploration.StartExploration;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.in.game.CreateGameRequest;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.game.GameCreated;

/**
 * Initialize game for the player
 */
final public class InitializeGame implements Listener<StartExploration> {
    final private GamePlayer player;
    final private ExplorationMapService mapService;

    public InitializeGame(GamePlayer player, ExplorationMapService mapService) {
        this.player = player;
        this.mapService = mapService;
    }

    @Override
    public void on(StartExploration event) {
        player.send(new GameCreated(CreateGameRequest.Type.EXPLORATION));
        player.send(new Stats(player));

        player.join(
            mapService.load(player.position().map()) // @todo handle entity not found
        );
    }

    @Override
    public Class<StartExploration> event() {
        return StartExploration.class;
    }
}
