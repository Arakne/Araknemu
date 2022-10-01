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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.point;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.CircleArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.fight.AddBuff;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StealMovementPointHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private StealMovementPointHandler handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();
        fight.turnList().start();

        caster = player.fighter();
        target = other.fighter();

        target.move(fight.map().get(123));

        handler = new StealMovementPointHandler(fight, 127, 128);

        requestStack.clear();
    }

    @Test
    void buffFixed() {
        caster.characteristics().alter(Characteristic.WISDOM, 10000);
        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(77);
        Mockito.when(effect.min()).thenReturn(2);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        Optional<Buff> buffT = target.buffs().stream().filter(b -> b.effect().effect() == 127).findFirst();
        Optional<Buff> buffC = caster.buffs().stream().filter(b -> b.effect().effect() == 128).findFirst();

        assertTrue(buffT.isPresent());
        assertTrue(buffC.isPresent());
        assertEquals(2, buffT.get().effect().min());
        assertEquals(2, buffC.get().effect().min());
        assertEquals(1, target.characteristics().get(Characteristic.MOVEMENT_POINT));
        assertEquals(5, caster.characteristics().get(Characteristic.MOVEMENT_POINT));
        assertEquals(5, caster.turn().points().movementPoints());

        requestStack.assertAll(
            ActionEffect.buff(buffT.get(), -2),
            new AddBuff(buffT.get()),
            new Stats(caster.properties()),
            ActionEffect.buff(buffC.get(), 2),
            "GIE128;1;2;;0;;5;0"
        );
    }

    @Test
    void directEffectShouldBeTransformedToSingleTurnBuffForTargetAndAddTurnPointToCaster() {
        caster.characteristics().alter(Characteristic.WISDOM, 10000);
        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(127);
        Mockito.when(effect.min()).thenReturn(2);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.handle(scope, scope.effects().get(0));


        Optional<Buff> buffT = target.buffs().stream().filter(b -> b.effect().effect() == 127).findFirst();
        Optional<Buff> buffC = caster.buffs().stream().filter(b -> b.effect().effect() == 128).findFirst();

        assertTrue(buffT.isPresent());
        assertFalse(buffC.isPresent());
        assertEquals(2, buffT.get().effect().min());
        assertEquals(1, buffT.get().remainingTurns());
        assertEquals(1, target.characteristics().get(Characteristic.MOVEMENT_POINT));
        assertEquals(3, caster.characteristics().get(Characteristic.MOVEMENT_POINT));
        assertEquals(5, caster.turn().points().movementPoints());

        requestStack.assertAll(
            ActionEffect.buff(buffT.get(), -2),
            new AddBuff(buffT.get()),
            ActionEffect.addMovementPoints(caster, 2)
        );
    }

    @Test
    void applyOnSelfShouldBeIgnored() {
        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(127);
        Mockito.when(effect.min()).thenReturn(2);
        Mockito.when(effect.duration()).thenReturn(2);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, caster.cell());
        handler.buff(scope, scope.effects().get(0));

        Optional<Buff> buff = caster.buffs().stream().filter(b -> b.effect().effect() == 127).findFirst();

        assertFalse(buff.isPresent());
        requestStack.assertEmpty();
    }

    @Test
    void dodgedAllShouldNotAddBuff() {
        target.characteristics().alter(Characteristic.RESISTANCE_MOVEMENT_POINT, 1000);
        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(77);
        Mockito.when(effect.min()).thenReturn(2);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        assertFalse(target.buffs().stream().anyMatch(b -> b.effect().effect() == 127));
        assertFalse(caster.buffs().stream().anyMatch(b -> b.effect().effect() == 128));

        assertEquals(3, target.characteristics().get(Characteristic.MOVEMENT_POINT));
        assertEquals(3, caster.characteristics().get(Characteristic.MOVEMENT_POINT));
        assertEquals(3, caster.turn().points().movementPoints());

        requestStack.assertAll(new ActionEffect(309, caster, target.id(), 2));
        requestStack.assertNotContains(AddBuff.class);
    }

    @Test
    void partialDodge() {
        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(77);
        Mockito.when(effect.min()).thenReturn(3);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        Optional<Buff> buffT = target.buffs().stream().filter(b -> b.effect().effect() == 127).findFirst();
        Optional<Buff> buffC = caster.buffs().stream().filter(b -> b.effect().effect() == 128).findFirst();

        assertTrue(buffT.isPresent());
        assertTrue(buffC.isPresent());
        assertBetween(1, 2, buffT.get().effect().min());
        assertBetween(1, 2, buffC.get().effect().min());
        assertEquals(3 - buffT.get().effect().min(), target.characteristics().get(Characteristic.MOVEMENT_POINT));
        assertEquals(3 + buffC.get().effect().min(), caster.characteristics().get(Characteristic.MOVEMENT_POINT));
        assertEquals(3 + buffC.get().effect().min(), caster.turn().points().movementPoints());

        requestStack.assertAll(
            new ActionEffect(309, caster, target.id(), 3 - buffT.get().effect().min()),
            ActionEffect.buff(buffT.get(), -buffT.get().effect().min()),
            new AddBuff(buffT.get()),
            new Stats(caster.properties()),
            ActionEffect.buff(buffC.get(), buffC.get().effect().min()),
            "GIE128;1;2;;0;;5;0"
        );
    }

    @Test
    void withArea() {
        fight.cancel(true);

        configureFight(fb -> fb
            .addSelf(b -> b.cell(165))
            .addEnemy(b -> b.cell(150))
            .addEnemy(b -> b.cell(151))
        );

        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(77);
        Mockito.when(effect.min()).thenReturn(1000);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(fight.fighters().get(0), spell, effect, fight.map().get(150));
        handler.buff(scope, scope.effects().get(0));

        Optional<Buff> buff0 = fight.fighters().get(0).buffs().stream().filter(b -> b.effect().effect() == 128).findFirst();
        Optional<Buff> buff1 = fight.fighters().get(1).buffs().stream().filter(b -> b.effect().effect() == 127).findFirst();
        Optional<Buff> buff2 = fight.fighters().get(2).buffs().stream().filter(b -> b.effect().effect() == 127).findFirst();

        assertTrue(buff0.isPresent());
        assertTrue(buff1.isPresent());
        assertTrue(buff2.isPresent());

        assertEquals(9, fight.fighters().get(0).characteristics().get(Characteristic.MOVEMENT_POINT));
        assertEquals(0, fight.fighters().get(1).characteristics().get(Characteristic.MOVEMENT_POINT));
        assertEquals(0, fight.fighters().get(2).characteristics().get(Characteristic.MOVEMENT_POINT));
    }

    @Test
    void buffStartAndTerminated() {
        caster.characteristics().alter(Characteristic.WISDOM, 10000);
        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(127);
        Mockito.when(effect.min()).thenReturn(2);
        Mockito.when(effect.duration()).thenReturn(1);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        assertEquals(1, target.characteristics().get(Characteristic.MOVEMENT_POINT));
        assertEquals(5, caster.characteristics().get(Characteristic.MOVEMENT_POINT));

        requestStack.clear();

        target.buffs().refresh();
        caster.buffs().refresh();
        caster.buffs().refresh();

        assertEquals(3, target.characteristics().get(Characteristic.MOVEMENT_POINT));
        assertEquals(3, caster.characteristics().get(Characteristic.MOVEMENT_POINT));
    }

    private void configureFight(Consumer<FightBuilder> configurator) {
        FightBuilder builder = fightBuilder();
        configurator.accept(builder);

        fight = builder.build(true);
        handler = new StealMovementPointHandler(fight, 127, 128);

        fight.nextState();

        requestStack.clear();
    }
}
