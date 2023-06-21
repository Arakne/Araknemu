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

import fr.quatrevieux.araknemu.data.constant.Characteristic;
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

class SwitchPositionOnAttackHandlerTest extends FightBaseCase {
    private Fight fight;
    private SwitchPositionOnAttackHandler handler;

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
    void buffShouldIgnoreSelf() {
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

        FightCastScope scope = makeCastScope(caster, spell, effect, caster.cell());
        handler.buff(scope, scope.effects().get(0));

        Optional<Buff> found = target.buffs().stream().filter(buff -> buff.effect().equals(effect)).findFirst();

        assertFalse(found.isPresent());
    }

    @Test
    void buffWithAreaMultipleFighters() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(165))
            .addEnemy(b -> b.cell(150))
            .addEnemy(b -> b.cell(151))
        );

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 20)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(player.fighter(), spell, effect, fight.map().get(122));
        handler.buff(scope, scope.effects().get(0));

        assertTrue(getFighter(1).buffs().stream().anyMatch(buff -> buff.effect().equals(effect)));
        assertTrue(getFighter(2).buffs().stream().anyMatch(buff -> buff.effect().equals(effect)));
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
        assertEquals(15, caster.life().max() - caster.life().current());

        assertEquals(165, target.cell().id());
        assertEquals(150, caster.cell().id());

        assertEquals(caster, fight.map().get(150).fighter());
        assertEquals(target, fight.map().get(165).fighter());

        requestStack.assertOne(ActionEffect.teleport(caster, target, fight.map().get(165)));
        requestStack.assertOne(ActionEffect.teleport(caster, caster, fight.map().get(150)));
    }

    @Test
    void buffWithReflectedDamage() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(165).charac(Characteristic.COUNTER_DAMAGE, 5))
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

        FightCastScope damageScope = makeCastScope(target, spell, damageEffect, caster.cell());
        fight.effects().apply(damageScope);

        assertEquals(target.life().max(), target.life().current());
        assertEquals(15, caster.life().max() - caster.life().current());

        assertEquals(165, target.cell().id());
        assertEquals(150, caster.cell().id());

        assertEquals(caster, fight.map().get(150).fighter());
        assertEquals(target, fight.map().get(165).fighter());

        requestStack.assertOne(ActionEffect.teleport(caster, target, fight.map().get(165)));
        requestStack.assertOne(ActionEffect.teleport(caster, caster, fight.map().get(150)));

        requestStack.assertOne(ActionEffect.reflectedDamage(caster, 5));
        requestStack.assertOne(ActionEffect.alterLifePoints(caster, caster, -5));
    }

    private void configureFight(Consumer<FightBuilder> configurator) {
        FightBuilder builder = fightBuilder();
        configurator.accept(builder);

        fight = builder.build(true);
        handler = new SwitchPositionOnAttackHandler(fight);

        fight.nextState();

        requestStack.clear();
    }
}
