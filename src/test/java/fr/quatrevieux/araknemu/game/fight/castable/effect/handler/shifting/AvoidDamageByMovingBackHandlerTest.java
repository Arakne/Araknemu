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
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.CircleArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AvoidDamageByMovingBackHandlerTest extends FightBaseCase {
    private Fight fight;
    private AvoidDamageByMovingBackHandler handler;

    @Test
    void handleDirectEffectNotSupported() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(165))
            .addEnemy(b -> b.player(other).cell(150))
        );

        PlayerFighter caster = player.fighter();
        PlayerFighter target = other.fighter();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.min()).thenReturn(3);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        assertThrows(UnsupportedOperationException.class, () -> handler.handle(scope, scope.effects().get(0)));
    }

    @Test
    void buffWillAddBuffToList() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(165))
            .addEnemy(b -> b.player(other).cell(150))
        );

        PlayerFighter caster = player.fighter();
        PlayerFighter target = other.fighter();

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
        configureFight(fb -> fb
            .addSelf(b -> b.cell(165))
            .addEnemy(b -> b.player(other).cell(150))
        );

        PlayerFighter caster = player.fighter();
        PlayerFighter target = other.fighter();

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
    void buffWithDirectDamage() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(165))
            .addEnemy(b -> b.player(other).cell(150))
        );

        PlayerFighter caster = player.fighter();
        PlayerFighter target = other.fighter();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(100);
        Mockito.when(effect.max()).thenReturn(2);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        requestStack.clear();

        SpellEffect damageEffect = Mockito.mock(SpellEffect.class);
        Mockito.when(damageEffect.effect()).thenReturn(100);
        Mockito.when(damageEffect.min()).thenReturn(10);
        Mockito.when(damageEffect.area()).thenReturn(new CellArea());
        Mockito.when(damageEffect.target()).thenReturn(SpellEffectTarget.DEFAULT);

        FightCastScope damageScope = makeCastScope(caster, spell, damageEffect, target.cell());
        fight.effects().apply(damageScope);

        assertEquals(target.life().max(), target.life().current());
        assertEquals(120, target.cell().id());
        assertFalse(fight.map().get(150).hasFighter());

        requestStack.assertOne(ActionEffect.slide(caster, target, fight.map().get(120)));
    }

    @Test
    void attackFromDistantCellShouldBeIgnored() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(180))
            .addEnemy(b -> b.player(other).cell(150))
        );

        PlayerFighter caster = player.fighter();
        PlayerFighter target = other.fighter();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(100);
        Mockito.when(effect.max()).thenReturn(2);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        requestStack.clear();

        SpellEffect damageEffect = Mockito.mock(SpellEffect.class);
        Mockito.when(damageEffect.effect()).thenReturn(100);
        Mockito.when(damageEffect.min()).thenReturn(10);
        Mockito.when(damageEffect.area()).thenReturn(new CellArea());
        Mockito.when(damageEffect.target()).thenReturn(SpellEffectTarget.DEFAULT);

        FightCastScope damageScope = makeCastScope(caster, spell, damageEffect, target.cell());
        fight.effects().apply(damageScope);

        assertEquals(15, target.life().max() - target.life().current());
        assertEquals(150, target.cell().id());

        requestStack.assertOne(ActionEffect.alterLifePoints(caster, target, -15));
    }

    @Test
    void buffWithPoisonDamageShouldBeIgnored() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(165))
            .addEnemy(b -> b.player(other).cell(150))
        );

        PlayerFighter caster = player.fighter();
        PlayerFighter target = other.fighter();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(100);
        Mockito.when(effect.max()).thenReturn(2);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        requestStack.clear();

        SpellEffect damageEffect = Mockito.mock(SpellEffect.class);
        Mockito.when(damageEffect.effect()).thenReturn(100);
        Mockito.when(damageEffect.min()).thenReturn(10);
        Mockito.when(damageEffect.duration()).thenReturn(5);
        Mockito.when(damageEffect.area()).thenReturn(new CellArea());
        Mockito.when(damageEffect.target()).thenReturn(SpellEffectTarget.DEFAULT);

        FightCastScope damageScope = makeCastScope(caster, spell, damageEffect, target.cell());
        fight.effects().apply(damageScope);

        assertTrue(target.buffs().stream().anyMatch(buff -> buff.effect().equals(damageEffect)));
        assertEquals(150, target.cell().id());
    }

    @Test
    void randomChance() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(165))
            .addEnemy(b -> b.player(other).cell(150).currentLife(1000).maxLife(1000))
        );

        PlayerFighter caster = player.fighter();
        PlayerFighter target = other.fighter();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(50);
        Mockito.when(effect.max()).thenReturn(1);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        SpellEffect damageEffect = Mockito.mock(SpellEffect.class);
        Mockito.when(damageEffect.effect()).thenReturn(100);
        Mockito.when(damageEffect.min()).thenReturn(10);
        Mockito.when(damageEffect.area()).thenReturn(new CellArea());
        Mockito.when(damageEffect.target()).thenReturn(SpellEffectTarget.DEFAULT);


        int lastLife = target.life().current();
        int lostLifeCount = 0;
        int moveBackCount = 0;

        for (int i = 0; i < 100; ++i) {
            FightCastScope damageScope = makeCastScope(caster, spell, damageEffect, target.cell());
            fight.effects().apply(damageScope);

            if (lastLife > target.life().current()) {
                ++lostLifeCount;
                lastLife = target.life().current();
            }

            if (target.cell().id() != 150) {
                target.move(fight.map().get(150));
                ++moveBackCount;
            }
        }

        assertBetween(40, 60, lostLifeCount);
        assertEquals(100 - lostLifeCount, moveBackCount);
    }

    private void configureFight(Consumer<FightBuilder> configurator) {
        FightBuilder builder = fightBuilder();
        configurator.accept(builder);

        fight = builder.build(true);
        handler = new AvoidDamageByMovingBackHandler(fight);

        fight.nextState();

        requestStack.clear();
    }
}
