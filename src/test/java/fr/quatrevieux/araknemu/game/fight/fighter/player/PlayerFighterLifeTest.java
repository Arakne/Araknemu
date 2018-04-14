package fr.quatrevieux.araknemu.game.fight.fighter.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class PlayerFighterLifeTest extends GameBaseCase {
    private PlayerFighterLife life;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        life = new PlayerFighterLife(gamePlayer(true).life());
    }

    @Test
    void initAlreadyInitialised() {
        life.init();

        assertThrows(IllegalStateException.class, () -> life.init());
    }

    @Test
    void defaults() throws SQLException, ContainerException {
        assertEquals(gamePlayer().life().current(), life.current());
        assertEquals(gamePlayer().life().max(), life.max());
    }

    @Test
    void notInit() throws SQLException, ContainerException {
        gamePlayer().life().set(100);

        assertEquals(100, life.current());
        assertEquals(gamePlayer().life().max(), life.max());
    }

    @Test
    void initialized() throws SQLException, ContainerException {
        int current = life.current(),
            max = life.max()
        ;

        life.init();

        gamePlayer().life().set(5);

        assertEquals(current, life.current());
        assertEquals(max, life.max());
    }
}
