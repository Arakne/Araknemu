package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupPosition;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterLifeChanged;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.game.fight.team.MonsterGroupTeam;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.monster.environment.LivingMonsterGroupPosition;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroupFactory;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class BaseFighterLifeTest extends FightBaseCase {
    private BaseFighterLife life;
    private Fight fight;
    private MonsterFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMonsterTemplates()
            .pushMonsterSpells()
        ;

        MonsterService service = container.get(MonsterService.class);

        MonsterGroupTeam team = new MonsterGroupTeam(
            new MonsterGroup(
                new LivingMonsterGroupPosition(
                    container.get(MonsterGroupFactory.class),
                    container.get(FightService.class),
                    new MonsterGroupPosition(new Position(10340, -1), 3),
                    new MonsterGroupData(3, 60000, 4, 3, Arrays.asList(new MonsterGroupData.Monster(31, new Interval(1, 100)), new MonsterGroupData.Monster(34, new Interval(1, 100)), new MonsterGroupData.Monster(36, new Interval(1, 100))), "")
                ),
                5,
                Collections.singletonList(service.load(31).all().get(2)),
                Direction.WEST,
                123
            ),
            Collections.singletonList(123),
            1
        );

        fighter = (MonsterFighter) team.fighters().stream().findFirst().get();
        fight = createFight();

        fighter.joinFight(fight, fight.map().get(123));

        life = new BaseFighterLife(fighter, 100);
    }

    @Test
    void defaults() {
        assertEquals(100, life.current());
        assertEquals(100, life.max());
        assertFalse(life.dead());
    }

    @Test
    void alterOnDamage() {
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
        life.alter(fighter, -50);

        AtomicReference<FighterLifeChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterLifeChanged.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(10, life.alter(caster, 10));
        assertEquals(60, life.current());

        assertSame(caster, ref.get().caster());
        assertSame(fighter, ref.get().fighter());
        assertEquals(10, ref.get().value());
    }

    @Test
    void alterOnHealHigherThanMax() {
        life.alter(fighter, -50);

        AtomicReference<FighterLifeChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterLifeChanged.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(50, life.alter(caster, 1000));
        assertEquals(life.max(), life.current());

        assertSame(caster, ref.get().caster());
        assertSame(fighter, ref.get().fighter());
        assertEquals(50, ref.get().value());
    }

    /**
     * #56 : Dot not heal when dead
     */
    @Test
    void alterHealIfDead() {
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
