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

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.LineArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoveBackHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private MoveBackHandler handler;

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

        CastScope scope = makeCastScope(caster, spell, effect, target.cell());

        assertThrows(UnsupportedOperationException.class, () -> handler.buff(scope, scope.effects().get(0)));
    }

    @Test
    void applySuccess() {
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

        FightCell lastCell = target.cell();
        FightCell destination = fight.map().get(105);

        CastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.handle(scope, scope.effects().get(0));

        requestStack.assertLast(ActionEffect.slide(caster, target, destination));

        assertFalse(lastCell.fighter().isPresent());
        assertSame(target, destination.fighter().get());
        assertSame(destination, target.cell());
    }

    @ParameterizedTest
    @MethodSource("provideDistances")
    void applyBlocked(int distance, int min, int max) {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(210))
            .addEnemy(b -> b.player(other).cell(182).currentLife(1000).maxLife(1000))
        );

        caster = player.fighter();
        target = other.fighter();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.min()).thenReturn(distance);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCell destination = fight.map().get(168);

        CastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.handle(scope, scope.effects().get(0));

        int damage = target.life().max() - target.life().current();
        assertBetween(min, max, damage);

        requestStack.assertOne(ActionEffect.slide(caster, target, destination));
        requestStack.assertOne(ActionEffect.alterLifePoints(caster, target, -damage));
    }

    public static Stream<Arguments> provideDistances() {
        return Stream.of(
            Arguments.of(2, 9, 16),
            Arguments.of(3, 18, 32),
            Arguments.of(4, 27, 48),
            Arguments.of(10, 81, 144)
        );
    }

    @Test
    void applyBlockedByFighterShouldGetHalfDamage() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(210))
            .addEnemy(b -> b.cell(182).currentLife(1000).maxLife(1000))
            .addEnemy(b -> b.cell(168).currentLife(1000).maxLife(1000))
        );

        caster = player.fighter();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.min()).thenReturn(3);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = makeCastScope(caster, spell, effect, fight.map().get(182));
        handler.handle(scope, scope.effects().get(0));

        List<Fighter> enemies = new ArrayList<>(fight.team(1).fighters());

        int damage = enemies.get(0).life().max() - enemies.get(0).life().current();
        assertBetween(27, 48, damage);

        assertEquals(damage / 2, enemies.get(1).life().max() - enemies.get(1).life().current());
    }

    @Test
    void applyBlockedWithoutMovementShouldNotPerformMove() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(210))
            .addEnemy(b -> b.cell(168).currentLife(1000).maxLife(1000))
        );

        caster = player.fighter();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.min()).thenReturn(3);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = makeCastScope(caster, spell, effect, fight.map().get(168));
        handler.handle(scope, scope.effects().get(0));

        List<Fighter> enemies = new ArrayList<>(fight.team(1).fighters());

        int damage = enemies.get(0).life().max() - enemies.get(0).life().current();

        requestStack.assertAll(ActionEffect.alterLifePoints(caster, enemies.get(0), -damage));
        assertSame(fight.map().get(168), enemies.get(0).cell());
    }

    @Test
    void applyWithArea() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(340))
            .addEnemy(b -> b.cell(312))
            .addEnemy(b -> b.cell(298))
            .addEnemy(b -> b.cell(284))
        );

        caster = player.fighter();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new LineArea(new EffectArea(EffectArea.Type.LINE, 5)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.min()).thenReturn(2);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = makeCastScope(caster, spell, effect, fight.map().get(312));
        handler.handle(scope, scope.effects().get(0));

        List<PassiveFighter> enemies = new ArrayList<>(fight.team(1).fighters());

        // Start with most distant target
        requestStack.assertAll(
            ActionEffect.slide(caster, enemies.get(2), fight.map().get(256)),
            ActionEffect.slide(caster, enemies.get(1), fight.map().get(270)),
            ActionEffect.slide(caster, enemies.get(0), fight.map().get(284))
        );

        assertEquals(fight.map().get(284), enemies.get(0).cell());
        assertEquals(fight.map().get(270), enemies.get(1).cell());
        assertEquals(fight.map().get(256), enemies.get(2).cell());
    }

    private void configureFight(Consumer<FightBuilder> configurator) {
        FightBuilder builder = fightBuilder();
        configurator.accept(builder);

        fight = builder.build(true);
        handler = new MoveBackHandler(fight);

        fight.nextState();

        requestStack.clear();
    }
}
