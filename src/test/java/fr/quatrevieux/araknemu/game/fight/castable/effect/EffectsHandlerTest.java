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

package fr.quatrevieux.araknemu.game.fight.castable.effect;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.DamageHandler;
import fr.quatrevieux.araknemu.game.fight.module.CommonEffectsModule;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import fr.quatrevieux.araknemu.network.game.out.fight.AddBuff;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EffectsHandlerTest extends FightBaseCase {
    private Fight fight;
    private EffectsHandler handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();
        handler = new EffectsHandler();

        new CommonEffectsModule(fight).effects(handler);

        requestStack.clear();
    }

    @Test
    void applyUndefinedEffect() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(-1);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        handler.apply(makeCastScope(player.fighter(), spell, effect, fight.map().get(123)));

        requestStack.assertEmpty();
    }

    @Test
    void applyDamage() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(100);
        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        handler.apply(makeCastScope(player.fighter(), spell, effect, other.fighter().cell()));

        requestStack.assertLast(ActionEffect.alterLifePoints(player.fighter(), other.fighter(), -15));
    }

    @Test
    void applyDamageBuff() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(100);
        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        handler.apply(makeCastScope(player.fighter(), spell, effect, other.fighter().cell()));
        Optional<Buff> found = other.fighter().buffs().stream().filter(buff -> buff.effect().effect() == 100).findFirst();

        assertTrue(found.isPresent());
        assertEquals(player.fighter(), found.get().caster());
        assertEquals(other.fighter(), found.get().target());
        assertEquals(effect, found.get().effect());
        assertEquals(spell, found.get().action());
        assertInstanceOf(DamageHandler.class, found.get().hook());
        assertEquals(5, found.get().remainingTurns());
        requestStack.assertLast(new AddBuff(found.get()));
    }

    @Test
    void applyStealLife() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(95);
        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        player.fighter().life().alter(player.fighter(), -20);
        requestStack.clear();

        handler.apply(makeCastScope(player.fighter(), spell, effect, other.fighter().cell()));

        requestStack.assertAll(
            ActionEffect.alterLifePoints(player.fighter(), other.fighter(), -15),
            ActionEffect.alterLifePoints(player.fighter(), player.fighter(), 7)
        );
    }

    @Test
    void applyTeleport() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(4);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(true);
        requestStack.clear();

        handler.apply(makeCastScope(player.fighter(), spell, effect, fight.map().get(123)));

        requestStack.assertLast(ActionEffect.teleport(player.fighter(), player.fighter(), fight.map().get(123)));
        assertEquals(123, player.fighter().cell().id());
    }

    @Test
    void applyWillCallBuffOnCastTarget() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        BuffHook hook = Mockito.mock(BuffHook.class);
        Buff buff = new Buff(effect, spell, player.fighter(), player.fighter(), hook);
        other.fighter().buffs().add(buff);

        CastScope cast = makeCastScope(player.fighter(), spell, effect, other.fighter().cell());

        handler.apply(cast);

        Mockito.verify(hook).onCastTarget(buff, cast);
    }

    @Test
    void applyWithCastTargetChangedShouldCallOnCastTargetOnNewTarget() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        BuffHook hook = Mockito.mock(BuffHook.class);
        Buff buff = new Buff(effect, spell, player.fighter(), other.fighter(), hook);
        other.fighter().buffs().add(buff);

        BuffHook hook2 = Mockito.mock(BuffHook.class);
        Buff buff2 = new Buff(effect, spell, player.fighter(), player.fighter(), hook2);
        player.fighter().buffs().add(buff2);

        CastScope cast = makeCastScope(player.fighter(), spell, effect, other.fighter().cell());

        Mockito.when(hook.onCastTarget(buff, cast)).then((params) -> {
            params.getArgument(1, CastScope.class).replaceTarget(other.fighter(), player.fighter());
            return false;
        });

        handler.apply(cast);

        Mockito.verify(hook).onCastTarget(buff, cast);
        Mockito.verify(hook2).onCastTarget(buff2, cast);
    }
}
