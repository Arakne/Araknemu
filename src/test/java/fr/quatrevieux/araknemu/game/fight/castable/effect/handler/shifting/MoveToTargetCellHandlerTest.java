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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoveToTargetCellHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private MoveToTargetCellHandler handler;

    @Test
    void buffNotSupported() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(165))
            .addEnemy(b -> b.player(other).cell(150))
        );

        caster = player.fighter();
        target = other.fighter();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.min()).thenReturn(3);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope<Fighter> scope = makeCastScope(caster, spell, effect, target.cell());

        assertThrows(UnsupportedOperationException.class, () -> handler.buff(scope, scope.effects().get(0)));
    }

    @Test
    void applySuccess() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(235))
            .addEnemy(b -> b.player(other).cell(221))
        );

        caster = player.fighter();
        target = other.fighter();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCell lastCell = target.cell();
        FightCell destination = fight.map().get(179);

        CastScope<Fighter> scope = makeCastScope(caster, spell, effect, destination);
        handler.handle(scope, scope.effects().get(0));

        requestStack.assertLast(ActionEffect.slide(caster, target, destination));

        assertFalse(lastCell.fighter().isPresent());
        assertSame(target, destination.fighter().get());
        assertSame(destination, target.cell());
    }

    @Test
    void applyShouldIgnoreIfDistanceIsZero() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(235))
            .addEnemy(b -> b.player(other).cell(221))
        );

        caster = player.fighter();
        target = other.fighter();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCell lastCell = target.cell();

        CastScope<Fighter> scope = makeCastScope(caster, spell, effect, lastCell);
        handler.handle(scope, scope.effects().get(0));

        requestStack.assertNotContains(ActionEffect.class);

        assertTrue(lastCell.fighter().isPresent());
        assertSame(target, lastCell.fighter().get());
        assertSame(lastCell, target.cell());
    }

    @ParameterizedTest
    @MethodSource("provideTargets")
    void applyBlocked(int targetCell, int min, int max) {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(196))
            .addEnemy(b -> b.player(other).cell(182).currentLife(1000).maxLife(1000))
        );

        caster = player.fighter();
        target = other.fighter();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCell destination = fight.map().get(168);

        CastScope<Fighter> scope = makeCastScope(caster, spell, effect, fight.map().get(targetCell));
        handler.handle(scope, scope.effects().get(0));

        int damage = target.life().max() - target.life().current();
        assertBetween(min, max, damage);

        requestStack.assertOne(ActionEffect.slide(caster, target, destination));
        requestStack.assertOne(ActionEffect.alterLifePoints(caster, target, -damage));
    }

    public static Stream<Arguments> provideTargets() {
        return Stream.of(
            Arguments.of(126, 27, 48),
            Arguments.of(112, 36, 64),
            Arguments.of(98, 45, 80),
            Arguments.of(42, 81, 144)
        );
    }

    private void configureFight(Consumer<FightBuilder> configurator) {
        FightBuilder builder = fightBuilder();
        configurator.accept(builder);

        fight = builder.build(true);
        handler = new MoveToTargetCellHandler(fight);

        fight.nextState();

        requestStack.clear();
    }
}
