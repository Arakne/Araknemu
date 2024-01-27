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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.CircleArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DamageHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private DamageHandler handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();

        caster = player.fighter();
        target = other.fighter();

        target.move(fight.map().get(123));

        handler = new DamageHandler(Element.AIR, fight);

        requestStack.clear();
    }

    @Test
    void applyRandomEffect() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.max()).thenReturn(15);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.handle(scope, scope.effects().get(0));

        int damage = target.life().max() - target.life().current();

        assertBetween(10, 15, damage);

        requestStack.assertLast(ActionEffect.alterLifePoints(caster, target, -damage));
    }

    @Test
    void applyFixedEffect() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.handle(scope, scope.effects().get(0));

        int damage = target.life().max() - target.life().current();

        assertEquals(10, damage);

        requestStack.assertLast(ActionEffect.alterLifePoints(caster, target, -10));
    }

    @Test
    void applyBoostEffect() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        player.properties().characteristics().base().set(Characteristic.AGILITY, 100);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.handle(scope, scope.effects().get(0));

        int damage = target.life().max() - target.life().current();

        assertEquals(20, damage);
    }

    @Test
    void applyToEmptyCell() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, fight.map().get(5));
        handler.handle(scope, scope.effects().get(0));

        requestStack.assertEmpty();
    }

    @Test
    void applyToEmptyCellWithArea() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 2)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, fight.map().get(122));
        handler.handle(scope, scope.effects().get(0));

        int damage = target.life().max() - target.life().current();

        assertEquals(8, damage);

        requestStack.assertLast(ActionEffect.alterLifePoints(caster, target, -8));
    }

    @Test
    void applyWithAreaMultipleFighters() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 20)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, fight.map().get(122));
        handler.handle(scope, scope.effects().get(0));

        requestStack.assertOne(ActionEffect.alterLifePoints(caster, target, -8));
        requestStack.assertOne(ActionEffect.alterLifePoints(caster, caster, -10));
    }

    @Test
    void applyWithAreaShouldReduceDamageOnHigherDistance() {
        fight = fightBuilder()
            .addSelf(fb -> fb.cell(152))
            .addEnemy(fb -> fb.cell(166)) // 1
            .addEnemy(fb -> fb.cell(151)) // 2
            .addEnemy(fb -> fb.cell(165)) // 3
            .addEnemy(fb -> fb.cell(179)) // 4
            .addEnemy(fb -> fb.cell(222)) // 5
            .addEnemy(fb -> fb.cell(250)) // 7
            .addEnemy(fb -> fb.cell(292)) // 10
            .addEnemy(fb -> fb.cell(320)) // 12
            .build(true)
        ;

        fight.nextState();

        handler = new DamageHandler(Element.AIR, fight);
        caster = (PlayerFighter) fight.map().get(152).fighter();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(20);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 20)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, caster.cell());
        handler.handle(scope, scope.effects().get(0));

        requestStack.assertOne(ActionEffect.alterLifePoints(caster, caster, -20));
        requestStack.assertOne(ActionEffect.alterLifePoints(caster, fight.map().get(166).fighter(), -18));
        requestStack.assertOne(ActionEffect.alterLifePoints(caster, fight.map().get(151).fighter(), -16));
        requestStack.assertOne(ActionEffect.alterLifePoints(caster, fight.map().get(165).fighter(), -14));
        requestStack.assertOne(ActionEffect.alterLifePoints(caster, fight.map().get(179).fighter(), -13));
        requestStack.assertOne(ActionEffect.alterLifePoints(caster, fight.map().get(222).fighter(), -11));
        requestStack.assertOne(ActionEffect.alterLifePoints(caster, fight.map().get(250).fighter(), -9));
        requestStack.assertOne(ActionEffect.alterLifePoints(caster, fight.map().get(292).fighter(), -6));
        requestStack.assertOne(ActionEffect.alterLifePoints(caster, fight.map().get(320).fighter(), -5));
    }

    @Test
    void applyWithAreaShouldApplySameDamageOnSameDistanceFromTarget() {
        fight = fightBuilder()
            .addSelf(fb -> fb.cell(298))
            .addEnemy(fb -> fb.cell(312)) // 1
            .addEnemy(fb -> fb.cell(313)) // 1
            .addEnemy(fb -> fb.cell(340)) // 3
            .addEnemy(fb -> fb.cell(342)) // 3
            .build(true)
        ;

        fight.nextState();

        handler = new DamageHandler(Element.AIR, fight);
        caster = (PlayerFighter) fight.map().get(298).fighter();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.max()).thenReturn(20);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 20)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, caster.cell());
        handler.handle(scope, scope.effects().get(0));

        int centerDamage = caster.life().max() - caster.life().current();
        int firstDamage = fight.map().get(312).fighter().life().max() - fight.map().get(312).fighter().life().current();
        int secondDamage = fight.map().get(313).fighter().life().max() - fight.map().get(313).fighter().life().current();
        int thirdDamage = fight.map().get(340).fighter().life().max() - fight.map().get(340).fighter().life().current();
        int fourthDamage = fight.map().get(342).fighter().life().max() - fight.map().get(342).fighter().life().current();

        assertBetween(10, 20, centerDamage);

        assertEquals(firstDamage, secondDamage);
        assertTrue(centerDamage > secondDamage);

        assertEquals(thirdDamage, fourthDamage);
        assertTrue(secondDamage > fourthDamage);
    }

    @Test
    void applyWithAreaMultipleFightersShouldStopApplyingDamageIfFightEnds() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(1000);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 63)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.handle(scope, scope.effects().get(0));

        requestStack.assertOne(ActionEffect.alterLifePoints(caster, target, -50));
        requestStack.assertNotContainsPrefix("GA;100;1;1");

        assertTrue(target.dead());
        assertFalse(caster.dead());
        assertTrue(caster.life().isFull());
    }

    @Test
    void buffWillAddBuffToList() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        Optional<Buff> found = target.buffs().stream().filter(buff -> buff.effect().equals(effect)).findFirst();

        assertTrue(found.isPresent());
        assertEquals(caster, found.get().caster());
        assertEquals(target, found.get().target());
        assertEquals(effect, found.get().effect());
        assertEquals(spell, found.get().action());
        assertEquals(handler, found.get().hook());
        assertEquals(5, found.get().remainingTurns());
    }

    @Test
    void buffWithAreaMultipleFighters() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 20)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, fight.map().get(122));
        handler.buff(scope, scope.effects().get(0));

        assertTrue(caster.buffs().stream().anyMatch(buff -> buff.effect().equals(effect)));
        assertTrue(target.buffs().stream().anyMatch(buff -> buff.effect().equals(effect)));
    }

    @Test
    void onStartTurn() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.max()).thenReturn(15);

        assertTrue(handler.onStartTurn(new Buff(effect, Mockito.mock(Spell.class), caster, target, handler)));

        int damage = target.life().max() - target.life().current();

        assertBetween(10, 15, damage);

        requestStack.assertLast(ActionEffect.alterLifePoints(caster, target, -damage));
    }

    @Test
    void onStartTurnOnDie() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(100000);

        assertFalse(handler.onStartTurn(new Buff(effect, Mockito.mock(Spell.class), caster, target, handler)));
    }
}
