package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.area.AreaService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.event.GameJoined;
import fr.quatrevieux.araknemu.network.game.out.area.SubAreaList;

/**
 * Initialize map areas on join game
 */
final public class InitializeAreas implements Listener<GameJoined> {
    final private GamePlayer player;
    final private AreaService service;

    public InitializeAreas(GamePlayer player, AreaService service) {
        this.player = player;
        this.service = service;
    }

    @Override
    public void on(GameJoined event) {
        player.send(
            new SubAreaList(service.list())
        );
    }

    @Override
    public Class<GameJoined> event() {
        return GameJoined.class;
    }
}
