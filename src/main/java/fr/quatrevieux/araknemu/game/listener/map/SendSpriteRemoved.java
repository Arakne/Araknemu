package fr.quatrevieux.araknemu.game.listener.map;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.event.SpriteRemoveFromMap;
import fr.quatrevieux.araknemu.network.game.out.game.RemoveSprite;

/**
 * Send packet for removed sprites
 */
final public class SendSpriteRemoved implements Listener<SpriteRemoveFromMap> {
    final private ExplorationMap map;

    public SendSpriteRemoved(ExplorationMap map) {
        this.map = map;
    }

    @Override
    public void on(SpriteRemoveFromMap event) {
        map.send(
            new RemoveSprite(event.sprite())
        );
    }

    @Override
    public Class<SpriteRemoveFromMap> event() {
        return SpriteRemoveFromMap.class;
    }
}
