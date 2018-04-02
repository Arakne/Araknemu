package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.event.StartExploration;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Initialize game for the player
 */
final public class InitializeGame implements Listener<StartExploration> {
    final private ExplorationPlayer player;
    final private ExplorationMapService mapService;

    public InitializeGame(ExplorationPlayer player, ExplorationMapService mapService) {
        this.player = player;
        this.mapService = mapService;
    }

    @Override
    public void on(StartExploration event) {
        player.send(new Stats(player));

        player.join(
            mapService.load(player.position().map()) // @todo handle entity not found
        );

        player.send(Error.welcome());
    }

    @Override
    public Class<StartExploration> event() {
        return StartExploration.class;
    }
}
