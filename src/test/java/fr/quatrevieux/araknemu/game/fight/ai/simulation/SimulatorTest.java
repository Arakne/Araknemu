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

package fr.quatrevieux.araknemu.game.fight.ai.simulation;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.SpellEffectStub;
import fr.quatrevieux.araknemu.game.fight.ai.FighterAI;
import fr.quatrevieux.araknemu.game.fight.ai.action.logic.NullGenerator;
import fr.quatrevieux.araknemu.game.fight.ai.factory.ChainAiFactory;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.BuffEffectSimulator;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.DoubleFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.module.AiModule;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.BaseCriticalityStrategy;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulatorTest extends FightBaseCase {
    private Simulator simulator;
    private Fight fight;
    private PlayerFighter fighter;
    private FighterAI ai;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushFunctionalSpells();

        fight = createFight();
        fighter = player.fighter();
        fight.register(new AiModule(new ChainAiFactory(), fight, Mockito.mock(Logger.class)));
        simulator = container.get(Simulator.class);
        ai = new FighterAI(fighter, fight, new NullGenerator());
    }

    @Test
    void simulateWithoutSupportedEffects() {
        simulator = new Simulator(new BaseCriticalityStrategy());

        CastSimulation simulation = simulator.simulate(fighter.spells().get(3), ai, fighter, other.fighter().cell());

        assertEquals(0, simulation.alliesLife());
        assertEquals(0, simulation.enemiesLife());
        assertEquals(0, simulation.selfLife());
        assertEquals(0, simulation.alliesBoost());
        assertEquals(0, simulation.enemiesBoost());
        assertEquals(0, simulation.selfBoost());

        assertEquals(fighter.spells().get(3), simulation.spell());
        assertEquals(fighter, simulation.caster());
        assertEquals(other.fighter().cell(), simulation.target());
    }

    @Test
    void simulateAttack() {
        CastSimulation simulation = simulator.simulate(fighter.spells().get(3), ai, fighter, other.fighter().cell());

        assertEquals(0, simulation.alliesLife());
        assertEquals(-10.2, simulation.enemiesLife(), 0.1);
        assertEquals(-10.2, simulation.mainEnemyLife(), 0.1);
        assertEquals(0, simulation.selfLife());
        assertEquals(0, simulation.alliesBoost());
        assertEquals(0, simulation.enemiesBoost());
        assertEquals(0, simulation.selfBoost());
        assertEquals(0, simulation.mainEnemyBoost());
    }

    @Test
    void simulateAttackNotMainEnemy() {
        DoubleFighter invoc = new DoubleFighter(-10, other.fighter());
        fight.fighters().join(invoc, fight.map().get(142));

        CastSimulation simulation = simulator.simulate(fighter.spells().get(3), ai, fighter, invoc.cell());

        assertEquals(0, simulation.alliesLife());
        assertEquals(-10.2, simulation.enemiesLife(), 0.1);
        assertEquals(0, simulation.mainEnemyLife(), 0.1);
        assertEquals(0, simulation.selfLife());
        assertEquals(0, simulation.alliesBoost());
        assertEquals(0, simulation.enemiesBoost());
        assertEquals(0, simulation.selfBoost());
        assertEquals(0, simulation.mainEnemyBoost());
    }

    @Test
    void simulateAttackShouldIgnoreHiddenFighter() {
        other.fighter().setHidden(other.fighter(), true);
        CastSimulation simulation = simulator.simulate(fighter.spells().get(3), ai, fighter, other.fighter().cell());

        assertEquals(0, simulation.alliesLife());
        assertEquals(0, simulation.enemiesLife());
        assertEquals(0, simulation.selfLife());
        assertEquals(0, simulation.alliesBoost());
        assertEquals(0, simulation.enemiesBoost());
        assertEquals(0, simulation.selfBoost());
    }

    @Test
    void simulateAttackShouldLimitByTargetLife() {
        fighter.characteristics().alter(Characteristic.FIXED_DAMAGE, 1000);

        CastSimulation simulation = simulator.simulate(fighter.spells().get(3), ai, fighter, other.fighter().cell());

        assertEquals(-50, simulation.enemiesLife());
    }

    @Test
    void simulateBoost() {
        CastSimulation simulation = simulator.simulate(getSpell(42, 1), ai, fighter, fighter.cell());

        assertEquals(0, simulation.alliesLife());
        assertEquals(0, simulation.enemiesLife());
        assertEquals(0, simulation.selfLife());
        assertEquals(0, simulation.alliesBoost());
        assertEquals(0, simulation.enemiesBoost());
        assertEquals(79, simulation.selfBoost(), .1);
        assertEquals(0, simulation.mainEnemyBoost());
        assertEquals(0, simulation.mainEnemyLife());
        assertEquals(0, simulation.mainAllyBoost());
        assertEquals(0, simulation.mainAllyLife());
    }

    @Test
    void simulateBoostMainAlly() {
        DoubleFighter invoc = new DoubleFighter(-10, fighter);
        fight.fighters().join(invoc, fight.map().get(142));
        ai = new FighterAI(invoc, fight, new NullGenerator());

        CastSimulation simulation = simulator.simulate(getSpell(42, 1), ai, invoc, fighter.cell());

        assertEquals(0, simulation.alliesLife());
        assertEquals(0, simulation.enemiesLife());
        assertEquals(0, simulation.selfLife());
        assertEquals(79, simulation.alliesBoost(), .1);
        assertEquals(0, simulation.enemiesBoost());
        assertEquals(0, simulation.selfBoost());
        assertEquals(0, simulation.mainEnemyBoost());
        assertEquals(0, simulation.mainEnemyLife());
        assertEquals(79, simulation.mainAllyBoost(), .1);
        assertEquals(0, simulation.mainAllyLife());
    }

    @Test
    void simulateWithProbableEffects() {
        CastSimulation simulation = simulator.simulate(getSpell(109, 5), ai, fighter, other.fighter().cell());

        assertEquals(0, simulation.alliesLife());
        assertEquals(-23.5, simulation.enemiesLife(), 0.1);
        assertEquals(-23.5, simulation.mainEnemyLife(), 0.1);
        assertEquals(0, simulation.selfLife());
        assertEquals(0, simulation.alliesBoost());
        assertEquals(0, simulation.enemiesBoost());
        assertEquals(0, simulation.selfBoost());
        assertEquals(0, simulation.mainAllyBoost());
        assertEquals(0, simulation.mainAllyLife());
    }

    @Test
    void simulateWithProbableEffectsMainAlly() {
        DoubleFighter invoc = new DoubleFighter(-10, fighter);
        fight.fighters().join(invoc, fight.map().get(142));
        ai = new FighterAI(invoc, fight, new NullGenerator());

        CastSimulation simulation = simulator.simulate(getSpell(109, 5), ai, invoc, fighter.cell());

        assertEquals(-23.5, simulation.alliesLife(), .1);
        assertEquals(0, simulation.enemiesLife());
        assertEquals(0, simulation.mainEnemyLife());
        assertEquals(0, simulation.selfLife());
        assertEquals(0, simulation.alliesBoost());
        assertEquals(0, simulation.enemiesBoost());
        assertEquals(0, simulation.selfBoost());
        assertEquals(0, simulation.mainAllyBoost());
        assertEquals(-23.5, simulation.mainAllyLife(), .1);
    }

    @Test
    void simulateWithProbableEffectsNotMainEnemy() {
        DoubleFighter invoc = new DoubleFighter(-10, other.fighter());
        fight.fighters().join(invoc, fight.map().get(142));

        CastSimulation simulation = simulator.simulate(getSpell(109, 5), ai, fighter, invoc.cell());

        assertEquals(0, simulation.alliesLife());
        assertEquals(-23.5, simulation.enemiesLife(), 0.1);
        assertEquals(0, simulation.mainEnemyLife(), 0.1);
        assertEquals(0, simulation.selfLife());
        assertEquals(0, simulation.alliesBoost());
        assertEquals(0, simulation.enemiesBoost());
        assertEquals(0, simulation.selfBoost());
    }

    @Test
    void simulateShouldHandleCriticality() {
        assertEquals(-23.5, simulator.simulate(getSpell(109, 5), ai, fighter, other.fighter().cell()).enemiesLife(), 0.1);
        assertEquals(-23.5, simulator.simulate(getSpell(109, 5), ai, fighter, other.fighter().cell()).mainEnemyLife(), 0.1);

        fighter.characteristics().alter(Characteristic.CRITICAL_BONUS, 100);
        assertEquals(-36.5, simulator.simulate(getSpell(109, 5), ai, fighter, other.fighter().cell()).enemiesLife(), 0.1);
        assertEquals(-36.5, simulator.simulate(getSpell(109, 5), ai, fighter, other.fighter().cell()).mainEnemyLife(), 0.1);
    }

    @Test
    void simulatePoison() {
        CastSimulation simulation = simulator.simulate(getSpell(181, 5), ai, fighter, other.fighter().cell());

        assertEquals(0, simulation.alliesLife());
        assertEquals(-36.03, simulation.enemiesLife(), 0.1);
        assertEquals(-36.8, simulation.selfLife(), 0.1);
        assertEquals(0, simulation.alliesBoost());
        assertEquals(0, simulation.enemiesBoost());
        assertEquals(0, simulation.selfBoost());
    }

    @Test
    void simulateWithoutCriticalEffects() {
        assertEquals(-2000, simulator.simulate(getSpell(468, 5), ai, fighter, other.fighter().cell()).enemiesBoost());
    }

    @Test
    void scoreWithoutSupportedEffects() {
        simulator = new Simulator(new BaseCriticalityStrategy());

        SpellScore score = simulator.score(fighter.spells().get(3), fighter.characteristics());

        assertEquals(0, score.score());
        assertEquals(0, score.damage());
        assertEquals(0, score.heal());
        assertEquals(0, score.boost());
        assertEquals(0, score.debuff());
    }

    @Test
    void scoreAttack() {
        SpellScore score = simulator.score(getSpell(3, 5), fighter.characteristics());

        assertEquals(7, score.attackRange());
        assertEquals(22, score.score());
        assertEquals(22, score.damage());
        assertEquals(0, score.heal());
        assertEquals(0, score.boost());
        assertEquals(0, score.debuff());
        assertTrue(score.isAttack());
        assertFalse(score.isSuicide());
    }

    @Test
    void scoreHeal() {
        SpellScore score = simulator.score(getSpell(130, 5), fighter.characteristics());

        assertEquals(4, score.attackRange());
        assertEquals(12, score.score());
        assertEquals(0, score.damage());
        assertEquals(12, score.heal());
        assertEquals(0, score.boost());
        assertEquals(0, score.debuff());
        assertFalse(score.isAttack());
        assertTrue(score.isHeal());
        assertFalse(score.isSuicide());
    }

    @Test
    void scoreBoost() {
        SpellScore score = simulator.score(getSpell(42, 5), fighter.characteristics());

        assertEquals(1, score.attackRange());
        assertEquals(55, score.score());
        assertEquals(0, score.damage());
        assertEquals(0, score.heal());
        assertEquals(55, score.boost());
        assertEquals(0, score.debuff());
        assertFalse(score.isAttack());
        assertFalse(score.isHeal());
        assertFalse(score.isSuicide());
    }

    @Test
    void scoreDebuff() {
        SpellScore score = simulator.score(getSpell(81, 5), fighter.characteristics());

        assertEquals(8, score.attackRange());
        assertEquals(400, score.score());
        assertEquals(0, score.damage());
        assertEquals(0, score.heal());
        assertEquals(0, score.boost());
        assertEquals(400, score.debuff());
        assertFalse(score.isAttack());
        assertFalse(score.isHeal());
        assertFalse(score.isSuicide());
    }

    @Test
    void multipleEffects() {
        SpellScore score = simulator.score(getSpell(168, 5), fighter.characteristics());

        assertEquals(9, score.attackRange());
        assertEquals(43, score.score());
        assertEquals(13, score.damage());
        assertEquals(0, score.heal());
        assertEquals(0, score.boost());
        assertEquals(30, score.debuff());
        assertTrue(score.isAttack());
        assertFalse(score.isHeal());
        assertFalse(score.isSuicide());
    }

    @Test
    void applyReduceableDamageBuffsWithoutBuff() {
        simulator = new Simulator(new BaseCriticalityStrategy());

        Damage damage = new Damage(10, Element.EARTH);
        Damage returned = simulator.applyReduceableDamageBuffs(other.fighter(), damage);

        assertSame(damage, returned);
        assertEquals(10, returned.value());
        assertEquals(0, returned.reflectedDamage());
        assertEquals(0, returned.reducedDamage());
    }

    @Test
    void applyReduceableDamageBuffsWithBuffNotMatching() {
        other.fighter().buffs().add(
            new Buff(
                new SpellEffectStub(100, 10),
                Mockito.mock(Spell.class),
                other.fighter(),
                other.fighter(),
                new BuffHook() {}
            )
        );

        AtomicBoolean called = new AtomicBoolean(false);

        simulator = new Simulator(new BaseCriticalityStrategy());
        simulator.registerBuff(101, new BuffEffectSimulator() {
            @Override
            public Damage onReduceableDamage(Buff buff, FighterData target, Damage damage) {
                called.set(true);
                return damage;
            }
        });

        Damage damage = new Damage(10, Element.EARTH);
        Damage returned = simulator.applyReduceableDamageBuffs(other.fighter(), damage);

        assertSame(damage, returned);
        assertEquals(10, returned.value());
        assertFalse(called.get());
    }

    @Test
    void applyReduceableDamageBuffsWithBuffMatching() {
        other.fighter().buffs().add(
            new Buff(
                new SpellEffectStub(100, 10),
                Mockito.mock(Spell.class),
                other.fighter(),
                other.fighter(),
                new BuffHook() {}
            )
        );

        AtomicBoolean called = new AtomicBoolean(false);

        simulator = new Simulator(new BaseCriticalityStrategy());
        simulator.registerBuff(100, new BuffEffectSimulator() {
            @Override
            public Damage onReduceableDamage(Buff buff, FighterData target, Damage damage) {
                called.set(true);

                return damage.reduce(5);
            }
        });

        Damage damage = new Damage(10, Element.EARTH);
        Damage returned = simulator.applyReduceableDamageBuffs(other.fighter(), damage);

        assertSame(damage, returned);
        assertEquals(5, returned.value());
        assertEquals(5, returned.reducedDamage());
        assertTrue(called.get());
    }

    @Test
    void applyReduceableDamageBuffsWithBuffMatchingReturnedNewInstance() {
        other.fighter().buffs().add(
            new Buff(
                new SpellEffectStub(100, 20),
                Mockito.mock(Spell.class),
                other.fighter(),
                other.fighter(),
                new BuffHook() {}
            )
        );

        simulator = new Simulator(new BaseCriticalityStrategy());
        simulator.registerBuff(100, new BuffEffectSimulator() {
            @Override
            public Damage onReduceableDamage(Buff buff, FighterData target, Damage damage) {
                return new Damage(buff.effect().min(), Element.NEUTRAL);
            }
        });

        Damage damage = new Damage(10, Element.EARTH);
        Damage returned = simulator.applyReduceableDamageBuffs(other.fighter(), damage);

        assertNotSame(damage, returned);
        assertEquals(20, returned.value());
        assertEquals(Element.NEUTRAL, returned.element());
    }

    private Spell getSpell(int id, int level) {
        return container.get(SpellService.class).get(id).level(level);
    }
}
