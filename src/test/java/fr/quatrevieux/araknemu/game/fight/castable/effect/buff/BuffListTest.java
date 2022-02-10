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

package fr.quatrevieux.araknemu.game.fight.castable.effect.buff;

import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.ReflectedDamage;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.AddBuff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;

class BuffListTest extends FightBaseCase {
    private BuffList list;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        createFight();

        list = new BuffList(player.fighter());
        requestStack.clear();
    }

    @Test
    void add() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        BuffHook hook = Mockito.mock(BuffHook.class);

        Buff buff = new Buff(effect, Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook);

        list.add(buff);

        requestStack.assertLast(new AddBuff(buff));
        Mockito.verify(hook).onBuffStarted(buff);
        assertArrayEquals(new Buff[] {buff}, list.stream().toArray());
    }

    @Test
    void addWithDurationZero() {
        // #61 Buff removed on end turn
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        BuffHook hook = Mockito.mock(BuffHook.class);

        Mockito.when(effect.duration()).thenReturn(0);

        Buff buff = new Buff(effect, Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook);

        list.add(buff);

        assertEquals(1, buff.remainingTurns());
    }

    @Test
    void addMultiple() {
        Buff buff1 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), Mockito.mock(BuffHook.class));
        Buff buff2 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), Mockito.mock(BuffHook.class));
        Buff buff3 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), Mockito.mock(BuffHook.class));

        list.add(buff1);
        list.add(buff2);
        list.add(buff3);

        assertArrayEquals(new Buff[] {buff1, buff2, buff3}, list.stream().toArray());
    }

    @Test
    void addSelfBuffWillIncrementTurns() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        BuffHook hook = Mockito.mock(BuffHook.class);

        Mockito.when(effect.duration()).thenReturn(1);

        Buff buff = new Buff(effect, Mockito.mock(Spell.class), player.fighter(), player.fighter(), hook);

        list.add(buff);

        assertEquals(2, buff.remainingTurns());
    }

    @Test
    void onStartTurnWithoutBuff() {
        assertTrue(list.onStartTurn());
    }

    @Test
    void onStartTurnWithOneBuffWillReturnTheBuffHookResponse() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        BuffHook hook = Mockito.mock(BuffHook.class);
        Buff buff = new Buff(effect, Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook);

        Mockito.when(hook.onStartTurn(buff)).thenReturn(false);

        list.add(buff);

        assertFalse(list.onStartTurn());
        Mockito.verify(hook).onStartTurn(buff);
    }

    @Test
    void onStartTurnWithMultipleBuff() {
        BuffHook hook1, hook2, hook3;

        Buff buff1 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook1 = Mockito.mock(BuffHook.class));
        Buff buff2 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook2 = Mockito.mock(BuffHook.class));
        Buff buff3 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook3 = Mockito.mock(BuffHook.class));

        Mockito.when(hook1.onStartTurn(buff1)).thenReturn(true);
        Mockito.when(hook2.onStartTurn(buff2)).thenReturn(false);
        Mockito.when(hook3.onStartTurn(buff3)).thenReturn(true);

        list.add(buff1);
        list.add(buff2);
        list.add(buff3);

        assertFalse(list.onStartTurn());

        Mockito.verify(hook1).onStartTurn(buff1);
        Mockito.verify(hook2).onStartTurn(buff2);
        Mockito.verify(hook3).onStartTurn(buff3);
    }

    @Test
    void onEndTurn() {
        BuffHook hook1, hook2, hook3;

        Buff buff1 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook1 = Mockito.mock(BuffHook.class));
        Buff buff2 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook2 = Mockito.mock(BuffHook.class));
        Buff buff3 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook3 = Mockito.mock(BuffHook.class));

        list.add(buff1);
        list.add(buff2);
        list.add(buff3);

        list.onEndTurn();

        Mockito.verify(hook1).onEndTurn(buff1);
        Mockito.verify(hook2).onEndTurn(buff2);
        Mockito.verify(hook3).onEndTurn(buff3);
    }

    @Test
    void onCastTarget() {
        BuffHook hook1, hook2, hook3;

        Buff buff1 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook1 = Mockito.mock(BuffHook.class));
        Buff buff2 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook2 = Mockito.mock(BuffHook.class));
        Buff buff3 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook3 = Mockito.mock(BuffHook.class));

        list.add(buff1);
        list.add(buff2);
        list.add(buff3);

        CastScope cast = new CastScope(Mockito.mock(Spell.class), player.fighter(), null);

        Mockito.when(hook1.onCastTarget(buff1, cast)).thenReturn(true);
        Mockito.when(hook2.onCastTarget(buff2, cast)).thenReturn(true);
        Mockito.when(hook3.onCastTarget(buff3, cast)).thenReturn(true);

        assertTrue(list.onCastTarget(cast));

        Mockito.verify(hook1).onCastTarget(buff1, cast);
        Mockito.verify(hook2).onCastTarget(buff2, cast);
        Mockito.verify(hook3).onCastTarget(buff3, cast);
    }

    @Test
    void onCastTargetWithReturnFalseShouldStopNextHooks() {
        BuffHook hook1, hook2, hook3;

        Buff buff1 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook1 = Mockito.mock(BuffHook.class));
        Buff buff2 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook2 = Mockito.mock(BuffHook.class));
        Buff buff3 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook3 = Mockito.mock(BuffHook.class));

        list.add(buff1);
        list.add(buff2);
        list.add(buff3);

        CastScope cast = new CastScope(Mockito.mock(Spell.class), player.fighter(), null);

        Mockito.when(hook1.onCastTarget(buff1, cast)).thenReturn(true);
        Mockito.when(hook2.onCastTarget(buff2, cast)).thenReturn(false);
        Mockito.when(hook3.onCastTarget(buff3, cast)).thenReturn(true);

        assertFalse(list.onCastTarget(cast));

        Mockito.verify(hook1).onCastTarget(buff1, cast);
        Mockito.verify(hook2).onCastTarget(buff2, cast);
        Mockito.verify(hook3, Mockito.never()).onCastTarget(buff3, cast);
    }

    @Test
    void onDirectDamage() {
        BuffHook hook1, hook2, hook3;

        Buff buff1 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook1 = Mockito.mock(BuffHook.class));
        Buff buff2 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook2 = Mockito.mock(BuffHook.class));
        Buff buff3 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook3 = Mockito.mock(BuffHook.class));

        list.add(buff1);
        list.add(buff2);
        list.add(buff3);

        Damage damage = new Damage(10, Element.NEUTRAL);
        ActiveFighter fighter = Mockito.mock(ActiveFighter.class);

        list.onDirectDamage(fighter, damage);

        Mockito.verify(hook1).onDirectDamage(buff1, fighter, damage);
        Mockito.verify(hook2).onDirectDamage(buff2, fighter, damage);
        Mockito.verify(hook3).onDirectDamage(buff3, fighter, damage);
    }

    @Test
    void onIndirectDamage() {
        BuffHook hook1, hook2, hook3;

        Buff buff1 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook1 = Mockito.mock(BuffHook.class));
        Buff buff2 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook2 = Mockito.mock(BuffHook.class));
        Buff buff3 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook3 = Mockito.mock(BuffHook.class));

        list.add(buff1);
        list.add(buff2);
        list.add(buff3);

        Damage damage = new Damage(10, Element.NEUTRAL);
        ActiveFighter fighter = Mockito.mock(ActiveFighter.class);

        list.onIndirectDamage(fighter, damage);

        Mockito.verify(hook1).onIndirectDamage(buff1, fighter, damage);
        Mockito.verify(hook2).onIndirectDamage(buff2, fighter, damage);
        Mockito.verify(hook3).onIndirectDamage(buff3, fighter, damage);
    }

    @Test
    void onBuffDamage() {
        BuffHook hook1, hook2, hook3;

        Buff buff1 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook1 = Mockito.mock(BuffHook.class));
        Buff buff2 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook2 = Mockito.mock(BuffHook.class));
        Buff buff3 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook3 = Mockito.mock(BuffHook.class));

        list.add(buff1);
        list.add(buff2);
        list.add(buff3);

        Damage damage = new Damage(10, Element.NEUTRAL);
        Buff buff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), Mockito.mock(BuffHook.class));

        list.onBuffDamage(buff, damage);

        Mockito.verify(hook1).onBuffDamage(buff1, buff, damage);
        Mockito.verify(hook2).onBuffDamage(buff2, buff, damage);
        Mockito.verify(hook3).onBuffDamage(buff3, buff, damage);
    }

    @Test
    void onLifeAltered() {
        BuffHook hook1, hook2, hook3;

        Buff buff1 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook1 = Mockito.mock(BuffHook.class));
        Buff buff2 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook2 = Mockito.mock(BuffHook.class));
        Buff buff3 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook3 = Mockito.mock(BuffHook.class));

        list.add(buff1);
        list.add(buff2);
        list.add(buff3);

        list.onLifeAltered(10);

        Mockito.verify(hook1).onLifeAltered(buff1, 10);
        Mockito.verify(hook2).onLifeAltered(buff2, 10);
        Mockito.verify(hook3).onLifeAltered(buff3, 10);
    }

    @Test
    void onReflectedDamage() {
        BuffHook hook1, hook2, hook3;

        Buff buff1 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook1 = Mockito.mock(BuffHook.class));
        Buff buff2 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook2 = Mockito.mock(BuffHook.class));
        Buff buff3 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook3 = Mockito.mock(BuffHook.class));

        list.add(buff1);
        list.add(buff2);
        list.add(buff3);

        ReflectedDamage damage = new ReflectedDamage(new Damage(10, Element.NEUTRAL), player.fighter());

        list.onReflectedDamage(damage);

        Mockito.verify(hook1).onReflectedDamage(buff1, damage);
        Mockito.verify(hook2).onReflectedDamage(buff2, damage);
        Mockito.verify(hook3).onReflectedDamage(buff3, damage);
    }

    @Test
    void onCastDamage() {
        BuffHook hook1, hook2, hook3;

        Buff buff1 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook1 = Mockito.mock(BuffHook.class));
        Buff buff2 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook2 = Mockito.mock(BuffHook.class));
        Buff buff3 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook3 = Mockito.mock(BuffHook.class));

        list.add(buff1);
        list.add(buff2);
        list.add(buff3);

        Damage damage = new Damage(10, Element.NEUTRAL);

        list.onCastDamage(damage, other.fighter());

        Mockito.verify(hook1).onCastDamage(buff1, damage, other.fighter());
        Mockito.verify(hook2).onCastDamage(buff2, damage, other.fighter());
        Mockito.verify(hook3).onCastDamage(buff3, damage, other.fighter());
    }

    @Test
    void onEffectValueCast() {
        BuffHook hook1, hook2, hook3;

        Buff buff1 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook1 = Mockito.mock(BuffHook.class));
        Buff buff2 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook2 = Mockito.mock(BuffHook.class));
        Buff buff3 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook3 = Mockito.mock(BuffHook.class));

        list.add(buff1);
        list.add(buff2);
        list.add(buff3);

        EffectValue ev = EffectValue.create(Mockito.mock(SpellEffect.class), other.fighter(), other.fighter());

        list.onEffectValueCast(ev);

        Mockito.verify(hook1).onEffectValueCast(buff1, ev);
        Mockito.verify(hook2).onEffectValueCast(buff2, ev);
        Mockito.verify(hook3).onEffectValueCast(buff3, ev);
    }

    @Test
    void onEffectValueTarget() {
        BuffHook hook1, hook2, hook3;

        Buff buff1 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook1 = Mockito.mock(BuffHook.class));
        Buff buff2 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook2 = Mockito.mock(BuffHook.class));
        Buff buff3 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook3 = Mockito.mock(BuffHook.class));

        list.add(buff1);
        list.add(buff2);
        list.add(buff3);

        EffectValue ev = EffectValue.create(Mockito.mock(SpellEffect.class), other.fighter(), other.fighter());

        list.onEffectValueTarget(ev, player.fighter());

        Mockito.verify(hook1).onEffectValueTarget(buff1, ev, player.fighter());
        Mockito.verify(hook2).onEffectValueTarget(buff2, ev, player.fighter());
        Mockito.verify(hook3).onEffectValueTarget(buff3, ev, player.fighter());
    }

    @Test
    void refreshWillDecrementRemaingTurnsAndRemoveExpiredBuffs() {
        BuffHook hook1, hook2, hook3;

        SpellEffect effect1 = Mockito.mock(SpellEffect.class);
        SpellEffect effect2 = Mockito.mock(SpellEffect.class);
        SpellEffect effect3 = Mockito.mock(SpellEffect.class);

        Mockito.when(effect1.duration()).thenReturn(1);
        Mockito.when(effect2.duration()).thenReturn(2);
        Mockito.when(effect3.duration()).thenReturn(3);

        Buff buff1 = new Buff(effect1, Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook1 = Mockito.mock(BuffHook.class));
        Buff buff2 = new Buff(effect2, Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook2 = Mockito.mock(BuffHook.class));
        Buff buff3 = new Buff(effect3, Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook3 = Mockito.mock(BuffHook.class));

        list.add(buff1);
        list.add(buff2);
        list.add(buff3);

        list.refresh();

        assertArrayEquals(new Buff[] {buff2, buff3}, list.stream().toArray());

        Mockito.verify(hook1).onBuffTerminated(buff1);
    }

    @Test
    void addMultipleAndRemoveThoseThatCanBeRemoved(){
        BuffHook hook1, hook2, hook3; 
        Buff buff1 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook1 = Mockito.mock(BuffHook.class));
        Buff buff2 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook2 = Mockito.mock(BuffHook.class), false);
        Buff buff3 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook3 = Mockito.mock(BuffHook.class), true);

        list.add(buff1);
        list.add(buff2);
        list.add(buff3);

        assertTrue(list.removeAll());

        assertIterableEquals(Collections.singletonList(buff2), list);

        Mockito.verify(hook1).onBuffTerminated(buff1);
        Mockito.verify(hook2, Mockito.never()).onBuffTerminated(buff2);
        Mockito.verify(hook3).onBuffTerminated(buff3);

        assertFalse(list.removeAll());
    }

    @Test
    void removeAllWithoutUndispellableBuff(){
        BuffHook hook1, hook2, hook3;
        Buff buff1 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook1 = Mockito.mock(BuffHook.class));
        Buff buff2 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook2 = Mockito.mock(BuffHook.class));
        Buff buff3 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook3 = Mockito.mock(BuffHook.class));

        list.add(buff1);
        list.add(buff2);
        list.add(buff3);

        assertTrue(list.removeAll());

        assertIterableEquals(Collections.emptyList(), list);

        Mockito.verify(hook1).onBuffTerminated(buff1);
        Mockito.verify(hook2).onBuffTerminated(buff2);
        Mockito.verify(hook3).onBuffTerminated(buff3);

        assertFalse(list.removeAll());
    }

    @Test
    void removeByCaster(){
        BuffHook hook1, hook2, hook3;
        Buff buff1 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook1 = Mockito.mock(BuffHook.class));
        Buff buff2 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), player.fighter(), player.fighter(), hook2 = Mockito.mock(BuffHook.class), false);
        Buff buff3 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook3 = Mockito.mock(BuffHook.class), true);

        list.add(buff1);
        list.add(buff2);
        list.add(buff3);

        assertTrue(list.removeByCaster(other.fighter()));

        assertIterableEquals(Collections.singletonList(buff2), list);

        Mockito.verify(hook1).onBuffTerminated(buff1);
        Mockito.verify(hook2, Mockito.never()).onBuffTerminated(buff2);
        Mockito.verify(hook3).onBuffTerminated(buff3);

        assertFalse(list.removeByCaster(other.fighter()));
    }
}
