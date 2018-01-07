package fr.quatrevieux.araknemu.game.event.listener.map;

import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.exploration.NewSpriteOnMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;

import java.util.Collections;

/**
 * Send new sprites added on the current map
 */
final public class SendNewSprite implements Listener<NewSpriteOnMap> {
    final private ExplorationMap map;

    public SendNewSprite(ExplorationMap map) {
        this.map = map;
    }

    @Override
    public void on(NewSpriteOnMap event) {
        // Save the string value for optimisation
        String packet = new AddSprites(Collections.singleton(event.sprite())).toString();

        for (GamePlayer player : map.players()) {
            if (player.id() != event.sprite().id()) {
                player.send(packet);
            }
        }
    }

    @Override
    public Class<NewSpriteOnMap> event() {
        return NewSpriteOnMap.class;
    }
}
