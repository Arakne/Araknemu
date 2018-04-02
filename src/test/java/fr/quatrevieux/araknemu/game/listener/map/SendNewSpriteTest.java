package fr.quatrevieux.araknemu.game.listener.map;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.event.NewSpriteOnMap;
import fr.quatrevieux.araknemu.game.player.PlayerSprite;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collections;

class SendNewSpriteTest extends GameBaseCase {
    private SendNewSprite listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendNewSprite(
            explorationPlayer().map()
        );
    }

    @Test
    void onSelfSprite() throws SQLException, ContainerException {
        requestStack.clear();

        listener.on(
            new NewSpriteOnMap(
                new PlayerSprite(gamePlayer())
            )
        );

        requestStack.assertEmpty();
    }

    @Test
    void onOtherSprite() throws Exception {
        Sprite sprite = new PlayerSprite(makeOtherPlayer());

        listener.on(
            new NewSpriteOnMap(sprite)
        );

        requestStack.assertLast(
            new AddSprites(
                Collections.singleton(sprite)
            )
        );
    }
}
