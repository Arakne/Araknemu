package fr.quatrevieux.araknemu.game.player.sprite;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class GamePlayerSpriteInfoTest extends GameBaseCase {
    @Test
    void values() throws SQLException, ContainerException {
        gamePlayer(true);

        GamePlayerSpriteInfo spriteInfo = new GamePlayerSpriteInfo(
            dataSet.refresh(new Player(gamePlayer().id())),
            gamePlayer().inventory()
        );

        assertEquals(gamePlayer().id(), spriteInfo.id());
        assertEquals(10, spriteInfo.gfxId());
        assertEquals("Bob", spriteInfo.name());
        assertArrayEquals(new Colors(123, 456, 789).toArray(), spriteInfo.colors().toArray());
        assertEquals("100x100", spriteInfo.size().toString());
        assertEquals(Sex.MALE, spriteInfo.sex());
        assertEquals(Race.FECA, spriteInfo.race());
        assertEquals(gamePlayer().inventory().accessories(), spriteInfo.accessories());
    }
}