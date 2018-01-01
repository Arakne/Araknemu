package fr.quatrevieux.araknemu.game.event.listener.player;

import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.exploration.NewSpriteOnMap;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;

import java.util.Collections;

/**
 * Send new sprites added on the current map
 */
final public class SendNewSprite implements Listener<NewSpriteOnMap> {
    final private GamePlayer player;

    public SendNewSprite(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(NewSpriteOnMap event) {
        if (event.sprite().id() != player.id()) {
            player.send(new AddSprites(Collections.singleton(event.sprite())));
        }
    }

    @Override
    public Class<NewSpriteOnMap> event() {
        return NewSpriteOnMap.class;
    }
}
