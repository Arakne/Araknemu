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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.CircleArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import fr.quatrevieux.araknemu.network.game.out.fight.CellShown;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterPositions;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InvisibilityHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private InvisibilityHandler handler;

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

        handler = new InvisibilityHandler(fight);

        requestStack.clear();
    }

    @Test
    void handle() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, caster.cell());
        assertThrows(UnsupportedOperationException.class, () -> handler.handle(scope, scope.effects().get(0)));
    }

    @Test
    void buffWillAddBuffToListAndHideTarget() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.special()).thenReturn(1234);
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
        assertTrue(target.hidden());

        requestStack.assertOne(new ActionEffect(150, caster, target.id(), 1));
    }

    @Test
    void buffWithAreaMultipleFighters() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.special()).thenReturn(1234);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 20)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, fight.map().get(122));
        handler.buff(scope, scope.effects().get(0));

        requestStack.assertOne(new ActionEffect(150, caster, target.id(), 1));
        requestStack.assertOne(new ActionEffect(150, caster, caster.id(), 1));

        assertTrue(caster.buffs().stream().anyMatch(buff -> buff.effect().equals(effect)));
        assertTrue(target.buffs().stream().anyMatch(buff -> buff.effect().equals(effect)));
    }

    @Test
    void buffTerminatedShouldMakeFighterVisible() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.special()).thenReturn(1234);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        assertTrue(target.hidden());
        requestStack.assertOne(new ActionEffect(150, caster, target.id(), 1));

        target.buffs().removeAll();

        assertFalse(target.hidden());
        requestStack.assertOne(new ActionEffect(150, caster, target.id(), 0));
        requestStack.assertOne(new FighterPositions(Collections.singleton(target)));
    }

    @Test
    void onCastDirectAttackShouldMakeFighterVisible() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.special()).thenReturn(1234);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        assertTrue(target.hidden());
        requestStack.assertOne(new ActionEffect(150, caster, target.id(), 1));

        SpellEffect attackEffect = Mockito.mock(SpellEffect.class);

        Mockito.when(attackEffect.area()).thenReturn(new CellArea());
        Mockito.when(attackEffect.effect()).thenReturn(97);
        Mockito.when(attackEffect.target()).thenReturn(SpellEffectTarget.DEFAULT);

        FightCastScope attackScope = makeCastScope(caster, spell, attackEffect, target.cell());
        target.buffs().onCast(attackScope);

        assertFalse(target.hidden());
        requestStack.assertOne(new FighterPositions(Collections.singleton(target)));
        requestStack.assertOne(new ActionEffect(150, target, target.id(), 0));
    }

    // See #301: do not show cell nor reveal caster if the cast is indirect (e.g. glyph, trap)
    @Test
    void onCastIndirectAttackShouldNotMakeFighterVisible() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.special()).thenReturn(1234);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = FightCastScope.fromCell(spell, caster, target.cell(), target.cell(), Collections.singletonList(effect));
        handler.buff(scope, scope.effects().get(0));

        assertTrue(target.hidden());
        requestStack.assertOne(new ActionEffect(150, caster, target.id(), 1));

        SpellEffect attackEffect = Mockito.mock(SpellEffect.class);

        Mockito.when(attackEffect.area()).thenReturn(new CellArea());
        Mockito.when(attackEffect.effect()).thenReturn(97);
        Mockito.when(attackEffect.target()).thenReturn(SpellEffectTarget.DEFAULT);

        FightCastScope attackScope = FightCastScope.fromCell(spell, caster, target.cell(), target.cell(), Collections.singletonList(attackEffect));
        target.buffs().onCast(attackScope);

        assertTrue(target.hidden());
        requestStack.assertNotContainsPrefix(new FighterPositions(Collections.singleton(target)).toString());
        requestStack.assertNotContainsPrefix(new ActionEffect(150, target, target.id(), 0).toString());
    }

    @Test
    void onCastPoisonShouldShowCasterCell() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.special()).thenReturn(1234);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        assertTrue(target.hidden());
        requestStack.assertOne(new ActionEffect(150, caster, target.id(), 1));

        SpellEffect attackEffect = Mockito.mock(SpellEffect.class);

        Mockito.when(attackEffect.area()).thenReturn(new CellArea());
        Mockito.when(attackEffect.effect()).thenReturn(97);
        Mockito.when(attackEffect.duration()).thenReturn(2);
        Mockito.when(attackEffect.target()).thenReturn(SpellEffectTarget.DEFAULT);

        FightCastScope attackScope = makeCastScope(caster, spell, attackEffect, target.cell());
        target.buffs().onCast(attackScope);

        assertTrue(target.hidden());
        requestStack.assertOne(new CellShown(target, target.cell().id()));
    }

    @Test
    void onCastNotAttackShouldShowCasterCell() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.special()).thenReturn(1234);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        assertTrue(target.hidden());
        requestStack.assertOne(new ActionEffect(150, caster, target.id(), 1));

        SpellEffect attackEffect = Mockito.mock(SpellEffect.class);

        Mockito.when(attackEffect.area()).thenReturn(new CellArea());
        Mockito.when(attackEffect.effect()).thenReturn(84);
        Mockito.when(attackEffect.target()).thenReturn(SpellEffectTarget.DEFAULT);

        FightCastScope attackScope = makeCastScope(caster, spell, attackEffect, target.cell());
        target.buffs().onCast(attackScope);

        assertTrue(target.hidden());
        requestStack.assertOne(new CellShown(target, target.cell().id()));
    }

    @Test
    void onCastShouldNotShowFighterCellIfAlreadyVisible() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.special()).thenReturn(1234);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        assertTrue(target.hidden());
        requestStack.assertOne(new ActionEffect(150, caster, target.id(), 1));

        target.setHidden(target, false);
        requestStack.clear();

        SpellEffect attackEffect = Mockito.mock(SpellEffect.class);

        Mockito.when(attackEffect.area()).thenReturn(new CellArea());
        Mockito.when(attackEffect.effect()).thenReturn(84);
        Mockito.when(attackEffect.target()).thenReturn(SpellEffectTarget.DEFAULT);

        FightCastScope attackScope = makeCastScope(caster, spell, attackEffect, target.cell());
        target.buffs().onCast(attackScope);

        requestStack.assertEmpty();
    }

    @Test
    void buffTerminatedWithOtherBuffActiveShouldNotSetVisible() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.special()).thenReturn(1234);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        Mockito.when(effect.duration()).thenReturn(3);
        scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        assertTrue(target.hidden());
        requestStack.assertOne(new ActionEffect(150, caster, target.id(), 1));

        requestStack.clear();

        target.buffs().refresh();
        target.buffs().refresh();
        target.buffs().refresh();
        target.buffs().refresh();

        assertTrue(target.hidden());
        requestStack.assertEmpty();

        target.buffs().refresh();
        target.buffs().refresh();

        assertFalse(target.hidden());
        requestStack.assertAll(
            new FighterPositions(Collections.singleton(target)),
            new ActionEffect(150, caster, target.id(), 0)
        );
    }

    @Test
    void buffMultipleBuffTerminateOnSameTurn() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.special()).thenReturn(1234);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(3);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));
        handler.buff(scope, scope.effects().get(0));

        assertTrue(target.hidden());
        requestStack.clear();

        target.buffs().refresh();
        target.buffs().refresh();
        target.buffs().refresh();
        target.buffs().refresh();

        assertFalse(target.hidden());
        requestStack.assertAll(
            new FighterPositions(Collections.singleton(target)),
            new ActionEffect(150, caster, target.id(), 0)
        );
    }

    @Test
    void buffTerminatedWithOtherBuffNotInvisibilityShouldBeIgnored() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.special()).thenReturn(1234);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(3);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        SpellEffect otherBuffEffect = Mockito.mock(SpellEffect.class);

        Mockito.when(otherBuffEffect.effect()).thenReturn(144);
        Mockito.when(otherBuffEffect.duration()).thenReturn(10);

        target.buffs().add(new Buff(otherBuffEffect, Mockito.mock(Spell.class), target, target, Mockito.mock(BuffHook.class)));

        assertTrue(target.hidden());
        requestStack.clear();

        target.buffs().refresh();
        target.buffs().refresh();
        target.buffs().refresh();
        target.buffs().refresh();

        assertFalse(target.hidden());
        requestStack.assertAll(
            new FighterPositions(Collections.singleton(target)),
            new ActionEffect(150, caster, target.id(), 0)
        );
    }

    @Test
    void onCastWithTwoActiveBuffShouldOnlyShowCellOnce() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.special()).thenReturn(1234);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());

        SpellEffect otherBuffEffect = Mockito.mock(SpellEffect.class);
        Mockito.when(otherBuffEffect.effect()).thenReturn(144);
        Mockito.when(otherBuffEffect.duration()).thenReturn(10);
        target.buffs().add(new Buff(otherBuffEffect, Mockito.mock(Spell.class), target, target, Mockito.mock(BuffHook.class)));

        handler.buff(scope, scope.effects().get(0));
        handler.buff(scope, scope.effects().get(0));

        requestStack.clear();
        SpellEffect attackEffect = Mockito.mock(SpellEffect.class);

        Mockito.when(attackEffect.area()).thenReturn(new CellArea());
        Mockito.when(attackEffect.effect()).thenReturn(84);
        Mockito.when(attackEffect.target()).thenReturn(SpellEffectTarget.DEFAULT);

        FightCastScope attackScope = makeCastScope(caster, spell, attackEffect, target.cell());
        target.buffs().onCast(attackScope);

        assertTrue(target.hidden());
        requestStack.assertAll(new CellShown(target, target.cell().id()));
    }

    @Test
    void onCastUnitWithoutActiveBuffShouldShowCell() {
        SpellEffect otherBuffEffect = Mockito.mock(SpellEffect.class);
        Mockito.when(otherBuffEffect.effect()).thenReturn(144);
        Mockito.when(otherBuffEffect.duration()).thenReturn(10);
        Buff fakeBuff = new Buff(otherBuffEffect, Mockito.mock(Spell.class), target, target, Mockito.mock(BuffHook.class));

        target.setHidden(target, true);

        requestStack.clear();

        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);
        SpellEffect attackEffect = Mockito.mock(SpellEffect.class);

        Mockito.when(attackEffect.area()).thenReturn(new CellArea());
        Mockito.when(attackEffect.effect()).thenReturn(84);
        Mockito.when(attackEffect.duration()).thenReturn(3);
        Mockito.when(attackEffect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope attackScope = makeCastScope(caster, spell, attackEffect, target.cell());
        handler.onCast(fakeBuff, attackScope);

        assertTrue(target.hidden());
        requestStack.assertAll(new CellShown(target, target.cell().id()));
    }
}
