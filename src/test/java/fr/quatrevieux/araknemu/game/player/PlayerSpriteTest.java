package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class PlayerSpriteTest extends GameBaseCase {
    @Test
    void data() throws SQLException, ContainerException {
        GamePlayer player = gamePlayer();

        PlayerSprite sprite = new PlayerSprite(player);

        assertEquals(1, sprite.id());
        assertEquals(200, sprite.cell());
        assertEquals("Bob", sprite.name());
        assertEquals(Sprite.Type.PLAYER, sprite.type());
    }

    @Test
    void generateString() throws SQLException, ContainerException {
        assertEquals(
            "200;0;0;1;Bob;1;10^100x100;0;;7b;1c8;315;;;;;;;;",
            new PlayerSprite(gamePlayer()).toString()
        );
    }
}
