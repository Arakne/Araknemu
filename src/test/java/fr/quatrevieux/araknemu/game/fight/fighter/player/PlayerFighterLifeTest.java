package fr.quatrevieux.araknemu.game.fight.fighter.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterLifeChanged;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class PlayerFighterLifeTest extends FightBaseCase {
    private PlayerFighterLife life;
    private Fight fight;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();

        life = new PlayerFighterLife(gamePlayer(true).life(), fighter);
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
        assertFalse(life.dead());
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

    @Test
    void alterOnDamage() {
        player.life().set(100);
        life.init();

        AtomicReference<FighterLifeChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterLifeChanged.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(-10, life.alter(caster, -10));
        assertEquals(90, life.current());

        assertSame(caster, ref.get().caster());
        assertSame(fighter, ref.get().fighter());
        assertEquals(-10, ref.get().value());
    }

    @Test
    void alterOnDamageHigherThanCurrentLife() {
        player.life().set(100);
        life.init();

        AtomicReference<FighterLifeChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterLifeChanged.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(-100, life.alter(caster, -150));
        assertEquals(0, life.current());

        assertSame(caster, ref.get().caster());
        assertSame(fighter, ref.get().fighter());
        assertEquals(-100, ref.get().value());
    }

    @Test
    void alterOnHeal() {
        player.life().set(100);
        life.init();

        AtomicReference<FighterLifeChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterLifeChanged.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(10, life.alter(caster, 10));
        assertEquals(110, life.current());

        assertSame(caster, ref.get().caster());
        assertSame(fighter, ref.get().fighter());
        assertEquals(10, ref.get().value());
    }

    @Test
    void alterOnHealHigherThanMax() {
        player.life().set(100);
        life.init();

        AtomicReference<FighterLifeChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterLifeChanged.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(195, life.alter(caster, 1000));
        assertEquals(life.max(), life.current());

        assertSame(caster, ref.get().caster());
        assertSame(fighter, ref.get().fighter());
        assertEquals(195, ref.get().value());
    }

    /**
     * #56 : Dot not heal when dead
     */
    @Test
    void alterHealIfDead() {
        life.init();
        life.alter(fighter, -1000);

        AtomicReference<FighterLifeChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterLifeChanged.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(0, life.alter(caster, 1000));
        assertEquals(0, life.current());
        assertTrue(life.dead());
        assertNull(ref.get());
    }

    /**
     * #56 : Dot not heal when dead
     */
    @Test
    void alterDamageIfDead() {
        life.init();
        life.alter(fighter, -1000);

        AtomicReference<FighterLifeChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterLifeChanged.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(0, life.alter(caster, -1000));
        assertEquals(0, life.current());
        assertTrue(life.dead());
        assertNull(ref.get());
    }

    @Test
    void alterOnDie() {
        player.life().set(100);
        life.init();

        AtomicReference<FighterDie> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterDie.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);

        life.alter(caster, -1000);

        assertEquals(0, life.current());
        assertSame(caster, ref.get().caster());
        assertSame(fighter, ref.get().fighter());
        assertTrue(life.dead());
    }

    @Test
    void kill() {
        life.init();

        AtomicReference<FighterDie> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterDie.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);

        life.kill(caster);

        assertEquals(0, life.current());
        assertSame(caster, ref.get().caster());
        assertSame(fighter, ref.get().fighter());
        assertTrue(life.dead());
    }
}
