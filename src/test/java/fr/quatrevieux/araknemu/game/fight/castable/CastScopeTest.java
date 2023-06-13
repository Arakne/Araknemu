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

import fr.arakne.utils.maps.MapCell;
import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.CircleArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.LineArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

        CastScope<Fighter, FightCell> scope = FightCastScope.simple(action, caster, target, Collections.emptyList());

        assertSame(caster, scope.caster());
        assertSame(target, scope.target());
        assertSame(action, scope.action());
        assertSame(caster.cell(), scope.from());
    }

    @Test
    void gettersWithCustomFrom() {
        Fighter caster = Mockito.mock(Fighter.class);
        FightCell target = Mockito.mock(FightCell.class);
        FightCell from = Mockito.mock(FightCell.class);
        Castable action = Mockito.mock(Castable.class);

        CastScope<Fighter, FightCell> scope = FightCastScope.fromCell(action, caster, from, target, Collections.emptyList());

        assertSame(caster, scope.caster());
        assertSame(target, scope.target());
        assertSame(action, scope.action());
        assertSame(from, scope.from());
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

        CastScope<Fighter, FightCell> scope = FightCastScope.simple(spell, caster, target.cell(), Collections.singletonList(effect));

        assertCount(1, scope.effects());
        assertEquals(effect, scope.effects().get(0).effect());
        assertEquals(Collections.singleton(target), scope.effects().get(0).targets());
        assertEquals(Collections.singleton(target.cell()), scope.effects().get(0).cells());
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

        CastScope<Fighter, FightCell> scope = FightCastScope.simple(spell, caster, fight.map().get(123), Collections.singletonList(effect));

        assertCount(1, scope.effects());
        assertEquals(effect, scope.effects().get(0).effect());
        assertEquals(Collections.emptyList(), scope.effects().get(0).targets());
    }

    @Test
    void effectCellTargets() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 2)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(true);

        CastScope<Fighter, FightCell> scope = FightCastScope.simple(spell, caster, fight.map().get(123), Collections.singletonList(effect));

        assertCount(1, scope.effects());
        assertEquals(effect, scope.effects().get(0).effect());
        assertEquals(Collections.emptyList(), scope.effects().get(0).targets());
        assertIterableEquals(Arrays.asList(
            fight.map().get(93),
            fight.map().get(94),
            fight.map().get(95),
            fight.map().get(108),
            fight.map().get(109),
            fight.map().get(122),
            fight.map().get(123),
            fight.map().get(124),
            fight.map().get(137),
            fight.map().get(138),
            fight.map().get(151),
            fight.map().get(152),
            fight.map().get(153)
        ), scope.effects().get(0).cells().stream().sorted(Comparator.comparingInt(FightCell::id)).collect(Collectors.toList()));
    }

    @Test
    void resolveCellsWithCustomFromCell() {
        caster.move(fight.map().get(152));

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new LineArea(new EffectArea(EffectArea.Type.LINE, 2)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(true);

        CastScope<Fighter, FightCell> scope = FightCastScope.fromCell(spell, caster, fight.map().get(227), fight.map().get(167), Collections.singletonList(effect));

        assertCount(1, scope.effects());
        assertIterableEquals(Arrays.asList(
            fight.map().get(137),
            fight.map().get(152),
            fight.map().get(167)
        ), scope.effects().get(0).cells().stream().sorted(Comparator.comparingInt(FightCell::id)).collect(Collectors.toList()));

        // For comparison, without custom cell: will resolve from caster cell
        scope = FightCastScope.simple(spell, caster, fight.map().get(167), Collections.singletonList(effect));

        assertCount(1, scope.effects());
        assertIterableEquals(Arrays.asList(
            fight.map().get(167),
            fight.map().get(182),
            fight.map().get(197)
        ), scope.effects().get(0).cells().stream().sorted(Comparator.comparingInt(FightCell::id)).collect(Collectors.toList()));
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

        CastScope<Fighter, FightCell> scope = FightCastScope.simple(spell, caster, fight.map().get(123), Collections.singletonList(effect));

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

        CastScope<Fighter, FightCell> scope = FightCastScope.simple(spell, caster, target.cell(), Collections.singletonList(effect));

        assertEquals(Collections.singleton(target), scope.effects().get(0).targets());
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

        CastScope<Fighter, FightCell> scope = FightCastScope.simple(spell, caster, fight.map().get(2), Collections.singletonList(effect));

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

        int c1 = 0, c2 = 0;

        for (int i = 0; i < 1000; ++i) {
            CastScope<Fighter, FightCell> scope = FightCastScope.probable(spell, caster, target.cell(), Arrays.asList(effect1, effect2));

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

        int c1 = 0, c2 = 0;

        for (int i = 0; i < 1000; ++i) {
            CastScope<Fighter, FightCell> scope = FightCastScope.probable(spell, caster, target.cell(), Arrays.asList(effect1, effect2));

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

        int count = 0;

        for (int i = 0; i < 1000; ++i) {
            CastScope<Fighter, FightCell> scope = FightCastScope.probable(spell, caster, target.cell(), Collections.singletonList(effect1));

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

        int count = 0;

        for (int i = 0; i < 1000; ++i) {
            CastScope<Fighter, FightCell> scope = FightCastScope.probable(spell, caster, target.cell(), Arrays.asList(effect1, effect2));

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
    void withRandomEffectsRouletteShouldAlwaysReturnAnEffect() throws SQLException {
        dataSet.pushFunctionalSpells();

        Spell spell = container.get(SpellService.class).get(101).level(5);

        for (int i = 0; i < 1000; ++i) {
            CastScope<Fighter, FightCell> scope = FightCastScope.probable(spell, caster, caster.cell(), spell.effects());

            assertCount(2, scope.effects()); // random effect + 1AP
        }
    }

    @Test
    void spell() {
        Spell spell = Mockito.mock(Spell.class);
        CastScope<Fighter, FightCell> scope = FightCastScope.simple(spell, caster, target.cell(), Collections.emptyList());

        assertEquals(spell, scope.spell());
    }

    @Test
    void spellNotASpell() {
        Castable action = Mockito.mock(Castable.class);
        CastScope<Fighter, FightCell> scope = FightCastScope.simple(action, caster, target.cell(), Collections.emptyList());

        assertNull(scope.spell());
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

        CastScope<Fighter, FightCell> scope = FightCastScope.simple(spell, caster, target.cell(), Collections.singletonList(effect));

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

        CastScope<Fighter, FightCell> scope = FightCastScope.simple(spell, caster, target.cell(), Collections.singletonList(effect));

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

        CastScope<Fighter, FightCell> scope = FightCastScope.simple(spell, caster, target.cell(), Collections.singletonList(effect));

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

        List<PlayableFighter> fighters = fight.turnList().fighters();

        CastScope<Fighter, FightCell> scope = FightCastScope.simple(spell, fighters.get(0), fight.map().get(263), Collections.singletonList(effect));

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

        List<PlayableFighter> fighters = fight.turnList().fighters();

        CastScope<Fighter, FightCell> scope = FightCastScope.simple(spell, fighters.get(0), fight.map().get(263), Collections.singletonList(effect));

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

        List<PlayableFighter> fighters = fight.turnList().fighters();

        CastScope<Fighter, FightCell> scope = FightCastScope.simple(spell, fighters.get(0), fight.map().get(263), Collections.singletonList(effect));

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

        List<PlayableFighter> fighters = fight.turnList().fighters();

        CastScope<Fighter, FightCell> scope = FightCastScope.simple(spell, fighters.get(0), fight.map().get(263), Collections.singletonList(effect));

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

        CastScope<Fighter, FightCell> scope = FightCastScope.simple(spell, caster, target.cell(), Collections.singletonList(effect));

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

        CastScope<Fighter, FightCell> scope = FightCastScope.simple(spell, caster, target.cell(), Collections.singletonList(effect));

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

        CastScope<Fighter, FightCell> scope = FightCastScope.simple(spell, caster, target.cell(), Collections.singletonList(effect));

        assertEquals(Collections.singleton(caster), scope.effects().get(0).targets());
    }
}
