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
 * Copyright (c) 2017-2021 Vincent Quatrevieux Jean-Alexandre Valentin
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CircleArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import fr.quatrevieux.araknemu.network.game.out.fight.AddBuff;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DispelHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private DispelHandler handler;

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

        handler = new DispelHandler(fight);

        requestStack.clear();
    }

    @Test
    void handle() {
        // cannot be debuff
        Buff buff_wisdom = makeWisdomBuffThatCannotBeDebuff();
        target.buffs().add(buff_wisdom);

        FightCastScope scope = makeDebuffSpell(target.cell());
        requestStack.clear();
        handler.handle(scope, scope.effects().get(0));

        Optional<Buff> buff1 = target.buffs().stream().filter(x -> x.effect().effect() == 124).findFirst();
        assertTrue(buff1.isPresent());
        requestStack.assertAll(
            "GA;132;1;2",
            new AddBuff(buff1.get())
        );
    }

    @Test
    void applyFromHook() {
        // can be debuff
        Buff toDispel = makeLuckBuffThatcanBeDispelled();
        target.buffs().add(toDispel);
        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);

        Mockito.when(effect.effect()).thenReturn(111);
        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(spell.constraints()).thenReturn(Mockito.mock(SpellConstraints.class));

        handler.applyFromHook(new Buff(effect, spell, caster, target, Mockito.mock(BuffHook.class)));

        requestStack.assertAll("GA;132;1;2");
        assertFalse(target.buffs().stream().anyMatch(x -> x.equals(toDispel)));
    }

    @Test
    void buff() {
        // cannot be debuff
        Buff buff_wisdom = makeWisdomBuffThatCannotBeDebuff();
        target.buffs().add(buff_wisdom);

        FightCastScope scope = makeDebuffSpell(target.cell());
        requestStack.clear();
        handler.buff(scope, scope.effects().get(0));

        Optional<Buff> buff1 = target.buffs().stream().filter(x -> x.effect().effect() == 124).findFirst();
        assertTrue(buff1.isPresent());
        requestStack.assertAll(
            "GA;132;1;2",
            new AddBuff(buff1.get())
        );
    }

    @Test
    void notAllBuffsAreRemoved() {
        // can be debuff
        Buff buff_luck = makeLuckBuffThatcanBeDispelled();
        // cannot be debuff
        Buff buff_wisdom = makeWisdomBuffThatCannotBeDebuff();

        caster.buffs().add(buff_luck);
        caster.buffs().add(buff_wisdom);

        FightCastScope scope = makeDebuffSpell(caster.cell());
        handler.handle(scope, scope.effects().get(0));

        Optional<Buff> buff1 = caster.buffs().stream().filter(x -> x.effect().effect() == 123).findFirst();
        Optional<Buff> buff2 = caster.buffs().stream().filter(x -> x.effect().effect() == 124).findFirst();

        assertFalse(buff1.isPresent());
        assertTrue(buff2.isPresent());
    }

    @Test
    void makeSureOnBuffTerminatedIsCalled() {
        BuffHook hook = Mockito.mock(BuffHook.class);
        SpellEffect effect1 = Mockito.mock(SpellEffect.class);

        Buff buff1 = new Buff(effect1, Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook);
        caster.buffs().add(buff1);
        assertArrayEquals(new Buff[] {buff1}, caster.buffs().stream().toArray());

        FightCastScope scope = makeDebuffSpell(caster.cell());
        handler.handle(scope, scope.effects().get(0));

        assertArrayEquals(new Buff[] {}, caster.buffs().stream().toArray());
        Mockito.verify(hook).onBuffTerminated(buff1);
    }

    private FightCastScope makeDebuffSpell(FightCell cell) {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);
        SpellEffect debuff = Mockito.mock(SpellEffect.class);

        Mockito.when(debuff.effect()).thenReturn(132);
        Mockito.when(debuff.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 1)));
        Mockito.when(debuff.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        return makeCastScope(caster, spell, debuff, cell);
    }

    private Buff makeLuckBuffThatcanBeDispelled() {
        SpellEffect effect_luck = Mockito.mock(SpellEffect.class);

        Mockito.when(effect_luck.effect()).thenReturn(123);
        Mockito.when(effect_luck.min()).thenReturn(50);
        Mockito.when(effect_luck.duration()).thenReturn(5);
        
        return new Buff(effect_luck, Mockito.mock(Spell.class), caster, caster, Mockito.mock(BuffHook.class));
    }

    private Buff makeWisdomBuffThatCannotBeDebuff() {
        SpellEffect effect_wisdom = Mockito.mock(SpellEffect.class);

        Mockito.when(effect_wisdom.effect()).thenReturn(124);
        Mockito.when(effect_wisdom.min()).thenReturn(50);
        Mockito.when(effect_wisdom.duration()).thenReturn(5);

        return new Buff(effect_wisdom, Mockito.mock(Spell.class), caster, caster, Mockito.mock(BuffHook.class), false);
    }
}
