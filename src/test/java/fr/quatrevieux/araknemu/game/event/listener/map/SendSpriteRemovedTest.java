package fr.quatrevieux.araknemu.game.event.listener.map;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.exploration.SpriteRemoveFromMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.network.game.out.game.RemoveSprite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SendSpriteRemovedTest extends GameBaseCase {
    private SendSpriteRemoved listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();
        listener = new SendSpriteRemoved(
            container.get(ExplorationMapService.class).load(10300)
        );
    }

    @Test
    void onSpriteRemoved() throws SQLException, ContainerException {
        explorationPlayer();

        Sprite sprite = Mockito.mock(Sprite.class);
        Mockito.when(sprite.id()).thenReturn(5);

        listener.on(
            new SpriteRemoveFromMap(sprite)
        );

        requestStack.assertLast(new RemoveSprite(sprite));
    }
}