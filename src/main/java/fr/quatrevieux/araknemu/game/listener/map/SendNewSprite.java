package fr.quatrevieux.araknemu.game.listener.map;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.event.NewSpriteOnMap;
import fr.quatrevieux.araknemu.game.world.creature.Operation;
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

        map.apply(new Operation() {
            @Override
            public void onExplorationPlayer(ExplorationPlayer player) {
                if (player.id() != event.sprite().id()) {
                    player.send(packet);
                }
            }
        });
    }

    @Override
    public Class<NewSpriteOnMap> event() {
        return NewSpriteOnMap.class;
    }
}
