/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterLifeChanged;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterMaxLifeChanged;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.game.fight.team.MonsterGroupTeam;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.monster.environment.LivingMonsterGroupPosition;
import fr.quatrevieux.araknemu.game.monster.environment.MonsterEnvironmentService;
import fr.quatrevieux.araknemu.game.monster.environment.RandomCellSelector;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroupFactory;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
                    container.get(MonsterEnvironmentService.class),
                    container.get(FightService.class),
                    new MonsterGroupData(3, Duration.ofMillis(60000), 4, 3, Arrays.asList(new MonsterGroupData.Monster(31, new Interval(1, 100), 1), new MonsterGroupData.Monster(34, new Interval(1, 100), 1), new MonsterGroupData.Monster(36, new Interval(1, 100), 1)), "", new Position(0, 0), false),
                    new RandomCellSelector(), false
                ),
                5,
                Collections.singletonList(service.load(31).all().get(2)),
                Direction.WEST,
                container.get(ExplorationMapService.class).load(10340).get(123),
                new Position(0, 0)
            ),
            Collections.singletonList(loadFightMap(10340).get(123)),
            1,
            container.get(FighterFactory.class)
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
    void damage() {
        AtomicReference<FighterLifeChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterLifeChanged.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(10, life.damage(caster, 10));
        assertEquals(90, life.current());

        assertSame(caster, ref.get().caster());
        assertSame(fighter, ref.get().fighter());
        assertEquals(-10, ref.get().value());
    }

    @Test
    void damageWithErosion() {
        life.alterErosion(10);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(10, life.damage(caster, 10));
        assertEquals(90, life.current());
        assertEquals(99, life.max());
    }

    @Test
    void damageWithErosionShouldTakeBaseDamageInAccount() {
        life.alterErosion(10);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(0, life.damage(caster, 0, 100));
        assertEquals(90, life.current());
        assertEquals(90, life.max());
    }

    @Test
    void damageWithErosionHigherThanActualDamage() {
        life.alterErosion(10);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(5, life.damage(caster, 5, 100));
        assertEquals(90, life.current());
        assertEquals(90, life.max());
    }

    @Test
    void damageWithErosionCantKill() {
        life.alterErosion(10);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(0, life.damage(caster, 0, 100000));
        assertEquals(1, life.current());
        assertEquals(1, life.max());
    }

    @Test
    void erosionCappedTo100() {
        life.alterErosion(1000);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(0, life.damage(caster, 0, 50));
        assertEquals(50, life.current());
        assertEquals(50, life.max());
    }

    @Test
    void erosionCappedTo0() {
        life.alterErosion(-1000);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(0, life.damage(caster, 0, 50));
        assertEquals(100, life.current());
        assertEquals(100, life.max());
    }

    @Test
    void alterErosion() {
        Fighter caster = Mockito.mock(Fighter.class);

        life.alterErosion(10);

        assertEquals(0, life.damage(caster, 0, 100));
        assertEquals(90, life.max());

        life.alterErosion(5);

        assertEquals(0, life.damage(caster, 0, 100));
        assertEquals(75, life.max());

        life.alterErosion(-10);

        assertEquals(0, life.damage(caster, 0, 100));
        assertEquals(70, life.max());
    }

    @Test
    void damageHigherThanCurrentLife() {
        AtomicReference<FighterLifeChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterLifeChanged.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(100, life.damage(caster, 150));
        assertEquals(0, life.current());

        assertSame(caster, ref.get().caster());
        assertSame(fighter, ref.get().fighter());
        assertEquals(-100, ref.get().value());
    }

    @Test
    void heal() {
        life.damage(fighter, 50);

        AtomicReference<FighterLifeChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterLifeChanged.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(10, life.heal(caster, 10));
        assertEquals(60, life.current());

        assertSame(caster, ref.get().caster());
        assertSame(fighter, ref.get().fighter());
        assertEquals(10, ref.get().value());
    }

    @Test
    void healHigherThanMax() {
        life.damage(fighter, 50);

        AtomicReference<FighterLifeChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterLifeChanged.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(50, life.heal(caster, 1000));
        assertEquals(life.max(), life.current());

        assertSame(caster, ref.get().caster());
        assertSame(fighter, ref.get().fighter());
        assertEquals(50, ref.get().value());
    }

    /**
     * #56 : Dot not heal when dead
     */
    @Test
    void healIfDead() {
        life.damage(fighter, 1000);

        AtomicReference<FighterLifeChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterLifeChanged.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(0, life.heal(caster, 1000));
        assertEquals(0, life.current());
        assertTrue(life.dead());
        assertNull(ref.get());
    }

    /**
     * #56 : Dot not heal when dead
     */
    @Test
    void damageIfDead() {
        life.damage(fighter, 1000);

        AtomicReference<FighterLifeChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterLifeChanged.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(0, life.damage(caster, 1000));
        assertEquals(0, life.current());
        assertTrue(life.dead());
        assertNull(ref.get());
    }

    @Test
    void damageOnDie() {
        AtomicReference<FighterDie> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterDie.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);

        life.damage(caster, 1000);

        assertEquals(0, life.current());
        assertSame(caster, ref.get().caster());
        assertSame(fighter, ref.get().fighter());
        assertTrue(life.dead());
    }

    @Test
    void healShouldCallOnLifeAlteredBuffs() {
        life.damage(fighter, 50);

        BuffHook hook = Mockito.mock(BuffHook.class);
        Buff buff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), fighter, fighter, hook);
        fighter.buffs().add(buff);

        Mockito.doCallRealMethod().when(hook).onHealApplied(Mockito.any(Buff.class), Mockito.anyInt());

        life.heal(fighter, 10);

        Mockito.verify(hook).onLifeAltered(buff, 10);
    }

    @Test
    void healShouldCallOnHealAppliedBuffs() {
        life.damage(fighter, 50);

        BuffHook hook = Mockito.mock(BuffHook.class);
        Buff buff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), fighter, fighter, hook);
        fighter.buffs().add(buff);

        life.heal(fighter, 10);

        Mockito.verify(hook).onHealApplied(buff, 10);
    }

    @Test
    void healShouldCallOnHealAppliedBuffsEvenIfNoEffect() {
        BuffHook hook = Mockito.mock(BuffHook.class);
        Buff buff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), fighter, fighter, hook);
        fighter.buffs().add(buff);

        life.heal(fighter, 10);

        Mockito.verify(hook).onHealApplied(buff, 0);
    }

    @Test
    void damageShouldCallOnLifeAlteredBuffs() {
        BuffHook hook = Mockito.mock(BuffHook.class);
        Buff buff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), fighter, fighter, hook);
        fighter.buffs().add(buff);

        Mockito.doCallRealMethod().when(hook).onDamageApplied(Mockito.any(Buff.class), Mockito.anyInt());

        life.damage(fighter, 10);

        Mockito.verify(hook).onLifeAltered(buff, -10);
    }

    @Test
    void damageShouldCallOnDamageAppliedBuffs() {
        BuffHook hook = Mockito.mock(BuffHook.class);
        Buff buff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), fighter, fighter, hook);
        fighter.buffs().add(buff);

        life.damage(fighter, 10);

        Mockito.verify(hook).onDamageApplied(buff, 10);
    }

    @Test
    void damageShouldCallOnDamageAppliedBuffsEvenWithNoDamage() {
        BuffHook hook = Mockito.mock(BuffHook.class);
        Buff buff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), fighter, fighter, hook);
        fighter.buffs().add(buff);

        life.damage(fighter, 0);

        Mockito.verify(hook).onDamageApplied(buff, 0);
    }

    @Test
    void damageShouldNotCallOnLifeAlteredBuffsWhenDie() {
        BuffHook hook = Mockito.mock(BuffHook.class);
        Buff buff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), fighter, fighter, hook);
        fighter.buffs().add(buff);

        life.damage(fighter, 1000);

        Mockito.verify(hook, Mockito.never()).onLifeAltered(Mockito.any(), Mockito.anyInt());
    }

    @Test
    void alterMaxPositive() {
        AtomicReference<FighterMaxLifeChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterMaxLifeChanged.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);
        life.alterMax(caster, 100);

        assertEquals(200, life.current());
        assertEquals(200, life.max());
        assertNotNull(ref.get());
        assertSame(caster, ref.get().caster());
        assertSame(fighter, ref.get().fighter());
    }

    @Test
    void alterMaxNotFullLife() {
        life.damage(fighter, 50);

        Fighter caster = Mockito.mock(Fighter.class);
        life.alterMax(caster, 100);

        assertEquals(150, life.current());
        assertEquals(200, life.max());
    }

    @Test
    void alterMaxNegative() {
        AtomicReference<FighterMaxLifeChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterMaxLifeChanged.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);
        life.alterMax(caster, -50);

        assertEquals(50, life.current());
        assertEquals(50, life.max());
        assertNotNull(ref.get());
        assertSame(caster, ref.get().caster());
        assertSame(fighter, ref.get().fighter());
    }

    @Test
    void alterMaxNegativeMoreThanCurrentLifeShouldKillFighter() {
        AtomicReference<FighterMaxLifeChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterMaxLifeChanged.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);
        life.alterMax(caster, -150);

        assertEquals(0, life.current());
        assertEquals(0, life.max());
        assertTrue(life.dead());
        assertNull(ref.get());
    }

    @Test
    void alterMaxDeadShouldDoNothing() {
        life.kill(fighter);

        AtomicReference<FighterMaxLifeChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterMaxLifeChanged.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);
        life.alterMax(caster, 100);

        assertEquals(0, life.current());
        assertEquals(100, life.max());
        assertTrue(life.dead());
        assertNull(ref.get());
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

        ref.set(null);
        life.kill(caster);

        assertNull(ref.get());
    }

    @Test
    void resuscitateNotDeadShouldDoNothing() {
        life.resuscitate(fighter, 10);

        assertEquals(100, life.current());
        assertEquals(100, life.max());
        assertFalse(life.dead());
    }

    @Test
    void resuscitateSuccessShouldSetCurrentLife() {
        life.kill(fighter);

        life.resuscitate(fighter, 10);

        assertEquals(10, life.current());
        assertEquals(100, life.max());
        assertFalse(life.dead());
    }

    @Test
    void resuscitateWithHigherThanMaxLifeShouldBeBoundedToMax() {
        life.kill(fighter);

        life.resuscitate(fighter, 10000);

        assertEquals(100, life.current());
        assertEquals(100, life.max());
        assertFalse(life.dead());
    }
}
