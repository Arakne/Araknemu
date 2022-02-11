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

package fr.quatrevieux.araknemu.game.fight.castable;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.CircleArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CastScopeTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();

        caster = player.fighter();
        target = other.fighter();
    }

    @Test
    void getters() {
        Fighter caster = Mockito.mock(Fighter.class);
        FightCell target = Mockito.mock(FightCell.class);
        Castable action = Mockito.mock(Castable.class);

        CastScope scope = new CastScope(action, caster, target);

        assertSame(caster, scope.caster());
        assertSame(target, scope.target());
        assertSame(action, scope.action());
    }

    @Test
    void withEffectsWillResolveTarget() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = new CastScope(spell, caster, target.cell());

        scope.withEffects(Collections.singletonList(effect));

        assertCount(1, scope.effects());
        assertEquals(effect, scope.effects().get(0).effect());
        assertEquals(Collections.singletonList(target), scope.effects().get(0).targets());
    }

    @Test
    void withEffectsWithFreeCellConstraintWillNotResolveTargets() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(true);

        CastScope scope = new CastScope(spell, caster, fight.map().get(123));

        scope.withEffects(Collections.singletonList(effect));

        assertCount(1, scope.effects());
        assertEquals(effect, scope.effects().get(0).effect());
        assertEquals(Collections.emptyList(), scope.effects().get(0).targets());
    }

    @Test
    void resolveTargetsWithAreaTwoFighters() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = new CastScope(spell, caster, fight.map().get(123));

        scope.withEffects(Collections.singletonList(effect));

        assertEquals(Arrays.asList(caster, target), scope.effects().get(0).targets());
    }

    @Test
    void resolveTargetsWithAreaOneFighter() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 2)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = new CastScope(spell, caster, target.cell());

        scope.withEffects(Collections.singletonList(effect));

        assertEquals(Arrays.asList(target), scope.effects().get(0).targets());
    }

    @Test
    void resolveTargetsWithAreaNoTargets() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 2)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = new CastScope(spell, caster, fight.map().get(2));

        scope.withEffects(Collections.singletonList(effect));

        assertEquals(Collections.emptyList(), scope.effects().get(0).targets());
    }

    @Test
    void withRandomEffectsOnlyProbableEffectsHalfProbability() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        SpellEffect effect1 = Mockito.mock(SpellEffect.class);
        SpellEffect effect2 = Mockito.mock(SpellEffect.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        Mockito.when(effect1.area()).thenReturn(new CellArea());
        Mockito.when(effect1.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect1.probability()).thenReturn(50);
        Mockito.when(effect2.area()).thenReturn(new CellArea());
        Mockito.when(effect2.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect2.probability()).thenReturn(50);

        CastScope scope = new CastScope(spell, caster, target.cell());

        int c1 = 0, c2 = 0;

        for (int i = 0; i < 1000; ++i) {
            scope.withRandomEffects(Arrays.asList(effect1, effect2));

            assertCount(1, scope.effects());

            if (effect1 == scope.effects().get(0).effect()) {
                ++c1;
            } else {
                ++c2;
            }
        }

        assertEquals(1000, c1 + c2);
        assertBetween(400, 600, c1);
        assertBetween(400, 600, c2);
    }

    @Test
    void withRandomEffectsOnlyProbableEffectsNotHalfProbability() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        SpellEffect effect1 = Mockito.mock(SpellEffect.class);
        SpellEffect effect2 = Mockito.mock(SpellEffect.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        Mockito.when(effect1.area()).thenReturn(new CellArea());
        Mockito.when(effect1.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect1.probability()).thenReturn(80);
        Mockito.when(effect2.area()).thenReturn(new CellArea());
        Mockito.when(effect2.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect2.probability()).thenReturn(20);

        CastScope scope = new CastScope(spell, caster, target.cell());

        int c1 = 0, c2 = 0;

        for (int i = 0; i < 1000; ++i) {
            scope.withRandomEffects(Arrays.asList(effect1, effect2));

            assertCount(1, scope.effects());

            if (effect1 == scope.effects().get(0).effect()) {
                ++c1;
            } else {
                ++c2;
            }
        }

        assertEquals(1000, c1 + c2);
        assertBetween(700, 900, c1);
        assertBetween(100, 300, c2);
    }

    @Test
    void withRandomEffectsWithOnProbableEffectCanBeNotChoose() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        SpellEffect effect1 = Mockito.mock(SpellEffect.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        Mockito.when(effect1.area()).thenReturn(new CellArea());
        Mockito.when(effect1.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect1.probability()).thenReturn(20);

        CastScope scope = new CastScope(spell, caster, target.cell());

        int count = 0;

        for (int i = 0; i < 1000; ++i) {
            scope.withRandomEffects(Collections.singletonList(effect1));

            if (scope.effects().size() == 1) {
                ++count;
            }
        }

        assertBetween(100, 300, count);
    }

    @Test
    void withRandomEffectsWithRandomAndPermanentEffects() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        SpellEffect effect1 = Mockito.mock(SpellEffect.class);
        SpellEffect effect2 = Mockito.mock(SpellEffect.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        Mockito.when(effect1.area()).thenReturn(new CellArea());
        Mockito.when(effect1.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect1.probability()).thenReturn(0);
        Mockito.when(effect2.area()).thenReturn(new CellArea());
        Mockito.when(effect2.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect2.probability()).thenReturn(50);

        CastScope scope = new CastScope(spell, caster, target.cell());

        int count = 0;

        for (int i = 0; i < 1000; ++i) {
            scope.withRandomEffects(Arrays.asList(effect1, effect2));

            if (scope.effects().size() == 1) {
                assertEquals(effect1, scope.effects().get(0).effect());
            } else {
                assertEquals(effect1, scope.effects().get(0).effect());
                assertEquals(effect2, scope.effects().get(1).effect());
                ++count;
            }
        }

        assertBetween(400, 600, count);
    }

    @Test
    void spell() {
        Spell spell = Mockito.mock(Spell.class);
        CastScope scope = new CastScope(spell, caster, target.cell());

        assertEquals(Optional.of(spell), scope.spell());
    }

    @Test
    void spellNotASpell() {
        Castable action = Mockito.mock(Castable.class);
        CastScope scope = new CastScope(action, caster, target.cell());

        assertEquals(Optional.empty(), scope.spell());
    }

    @Test
    void targets() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = new CastScope(spell, caster, target.cell());

        scope.withEffects(Collections.singletonList(effect));

        assertCollectionEquals(scope.targets(), target, caster);
        assertCollectionEquals(scope.effects().get(0).targets(), target, caster);
    }

    @Test
    void targetsWithDeadFighter() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = new CastScope(spell, caster, target.cell());

        scope.withEffects(Collections.singletonList(effect));

        target.init();
        target.life().kill(caster);

        assertCollectionEquals(scope.effects().get(0).targets(), caster);
    }

    @Test
    void replaceTarget() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = new CastScope(spell, caster, target.cell());

        scope.withEffects(Collections.singletonList(effect));
        scope.replaceTarget(target, caster);

        assertCollectionEquals(scope.targets(), caster, target);
        assertCollectionEquals(scope.effects().get(0).targets(), caster, caster);
    }

    @Test
    void replaceTargetChaining() {
        Fight fight = fightBuilder()
            .addSelf(fb -> fb.cell(277))
            .addEnemy(fb -> fb.cell(263))
            .addEnemy(fb -> fb.cell(249))
            .addEnemy(fb -> fb.cell(234))
            .build(true)
        ;

        fight.nextState();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        List<Fighter> fighters = fight.fighters();

        CastScope scope = new CastScope(spell, fighters.get(0), fight.map().get(263));

        scope.withEffects(Collections.singletonList(effect));
        scope.replaceTarget(fighters.get(1), fighters.get(2));
        scope.replaceTarget(fighters.get(2), fighters.get(3));

        assertCollectionEquals(scope.targets(), fighters.get(1), fighters.get(2), fighters.get(3));
        assertCollectionEquals(scope.effects().get(0).targets(), fighters.get(3));
    }

    @Test
    void replaceTargetChainingWithRecursionOnFirstTarget() {
        Fight fight = fightBuilder()
            .addSelf(fb -> fb.cell(277))
            .addEnemy(fb -> fb.cell(263))
            .addEnemy(fb -> fb.cell(249))
            .addEnemy(fb -> fb.cell(234))
            .build(true)
        ;

        fight.nextState();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        List<Fighter> fighters = fight.fighters();

        CastScope scope = new CastScope(spell, fighters.get(0), fight.map().get(263));

        scope.withEffects(Collections.singletonList(effect));
        scope.replaceTarget(fighters.get(1), fighters.get(2));
        scope.replaceTarget(fighters.get(2), fighters.get(3));
        scope.replaceTarget(fighters.get(3), fighters.get(1));

        assertCollectionEquals(scope.targets(), fighters.get(1), fighters.get(2), fighters.get(3));
        assertCollectionEquals(scope.effects().get(0).targets(), fighters.get(1));
    }

    @Test
    void replaceTargetChainingWithRecursionOnMiddleTarget() {
        Fight fight = fightBuilder()
            .addSelf(fb -> fb.cell(277))
            .addEnemy(fb -> fb.cell(263))
            .addEnemy(fb -> fb.cell(249))
            .addEnemy(fb -> fb.cell(234))
            .build(true)
        ;

        fight.nextState();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        List<Fighter> fighters = fight.fighters();

        CastScope scope = new CastScope(spell, fighters.get(0), fight.map().get(263));

        scope.withEffects(Collections.singletonList(effect));
        scope.replaceTarget(fighters.get(1), fighters.get(2));
        scope.replaceTarget(fighters.get(2), fighters.get(3));
        scope.replaceTarget(fighters.get(3), fighters.get(2));

        assertCollectionEquals(scope.targets(), fighters.get(1), fighters.get(2), fighters.get(3));
        assertCollectionEquals(scope.effects().get(0).targets(), fighters.get(2));
    }

    @Test
    void removeTargetWithReplaceTargetChain() {
        Fight fight = fightBuilder()
            .addSelf(fb -> fb.cell(277))
            .addEnemy(fb -> fb.cell(263))
            .addEnemy(fb -> fb.cell(249))
            .addEnemy(fb -> fb.cell(234))
            .build(true)
        ;

        fight.nextState();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        List<Fighter> fighters = fight.fighters();

        CastScope scope = new CastScope(spell, fighters.get(0), fight.map().get(263));

        scope.withEffects(Collections.singletonList(effect));
        scope.replaceTarget(fighters.get(1), fighters.get(2));
        scope.replaceTarget(fighters.get(2), fighters.get(3));
        scope.removeTarget(fighters.get(3));

        assertCollectionEquals(scope.targets(), fighters.get(1), fighters.get(2), fighters.get(3));
        assertTrue(scope.effects().get(0).targets().isEmpty());
    }

    @Test
    void removeTarget() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = new CastScope(spell, caster, target.cell());

        scope.withEffects(Collections.singletonList(effect));
        scope.removeTarget(target);

        assertCollectionEquals(scope.targets(), caster, target);
        assertCollectionEquals(scope.effects().get(0).targets(), caster);

        scope.removeTarget(caster);

        assertCollectionEquals(scope.targets(), caster, target);
        assertTrue(scope.effects().get(0).targets().isEmpty());
    }

    @Test
    void effectTargetsFilter() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(effect.target()).thenReturn(new SpellEffectTarget(SpellEffectTarget.NOT_SELF));
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = new CastScope(spell, caster, target.cell());

        scope.withEffects(Collections.singletonList(effect));

        assertCollectionEquals(scope.effects().get(0).targets(), target);
    }

    @Test
    void effectTargetsOnlyCaster() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(new SpellEffectTarget(SpellEffectTarget.ONLY_CASTER));
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = new CastScope(spell, caster, target.cell());

        scope.withEffects(Collections.singletonList(effect));

        assertEquals(Collections.singletonList(caster), scope.effects().get(0).targets());
    }
}
