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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.invocations;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.InvocationFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.module.SpiritualLeashModule;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FighterTurnOrder;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InvokeLastDeadFighterHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private InvokeLastDeadFighterHandler handler;
    private FightTurn turn;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();

        SpiritualLeashModule module = new SpiritualLeashModule(fight);
        fight.register(module);

        fight.nextState();

        caster = player.fighter();
        turn = new FightTurn(caster, fight, Duration.ofSeconds(30));
        turn.start();

        handler = new InvokeLastDeadFighterHandler(fight, module);

        requestStack.clear();
    }

    @Test
    void handleWithPlayerFighter() throws SQLException {
        PlayerFighter dead = makePlayerFighter(makeSimpleGamePlayer(40));
        dead.setTeam(player.fighter().team());
        fight.fighters().joinTurnList(dead, fight.map().get(200));
        dead.init();

        dead.life().kill(dead);

        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.min()).thenReturn(25);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(true);

        FightCastScope scope = makeCastScope(caster, spell, effect, fight.map().get(123));
        handler.handle(scope, scope.effects().get(0));

        assertSame(dead, fight.map().get(123).fighter());
        assertSame(player.fighter(), dead.invoker());
        assertEquals(12, dead.life().current());
        assertSame(fight.map().get(123), dead.cell());
        assertFalse(dead.dead());

        requestStack.assertAll(
            ActionEffect.packet(caster, new AddSprites(Collections.singletonList(dead.sprite()))),
            new ActionEffect(780, caster, "+" + dead.sprite()),
            ActionEffect.packet(caster, new FighterTurnOrder(fight.turnList()))
        );
    }

    @Test
    void handleWithoutDeadFighterShouldIgnore() throws SQLException {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.min()).thenReturn(25);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(true);

        FightCastScope scope = makeCastScope(caster, spell, effect, fight.map().get(123));
        handler.handle(scope, scope.effects().get(0));

        requestStack.assertEmpty();
    }

    @Test
    void handleShouldGiveAtLeast1LifePoint() throws SQLException {
        PlayerFighter dead = makePlayerFighter(makeSimpleGamePlayer(40));
        dead.setTeam(player.fighter().team());
        fight.fighters().joinTurnList(dead, fight.map().get(200));
        dead.init();

        dead.life().kill(dead);

        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.min()).thenReturn(1);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(true);

        FightCastScope scope = makeCastScope(caster, spell, effect, fight.map().get(123));
        handler.handle(scope, scope.effects().get(0));

        assertSame(dead, fight.map().get(123).fighter());
        assertSame(player.fighter(), dead.invoker());
        assertEquals(1, dead.life().current());
        assertSame(fight.map().get(123), dead.cell());
        assertFalse(dead.dead());

        requestStack.assertAll(
            ActionEffect.packet(caster, new AddSprites(Collections.singletonList(dead.sprite()))),
            new ActionEffect(780, caster, "+" + dead.sprite()),
            ActionEffect.packet(caster, new FighterTurnOrder(fight.turnList()))
        );
    }

    @Test
    void handleWithInvocation() throws SQLException {
        dataSet
            .pushMonsterTemplateInvocations()
            .pushMonsterSpellsInvocations()
        ;

        InvocationFighter dead = new InvocationFighter(
            -42,
            container.get(MonsterService.class).load(36).get(5),
            player.fighter().team(),
            player.fighter()
        );
        fight.fighters().joinTurnList(dead, fight.map().get(200));
        dead.init();

        dead.life().kill(dead);

        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.min()).thenReturn(25);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(true);

        FightCastScope scope = makeCastScope(caster, spell, effect, fight.map().get(123));
        handler.handle(scope, scope.effects().get(0));

        assertSame(dead, fight.map().get(123).fighter());
        assertSame(player.fighter(), dead.invoker());
        assertEquals(26, dead.life().current());
        assertSame(fight.map().get(123), dead.cell());
        assertFalse(dead.dead());

        requestStack.assertAll(
            new FighterTurnOrder(fight.turnList()),
            new ActionEffect(147, caster, "+" + dead.sprite()),
            new ActionEffect(780, caster, "+" + dead.sprite()),
            ActionEffect.packet(caster, new FighterTurnOrder(fight.turnList()))
        );
    }

    @Test
    void buff() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(true);

        FightCastScope scope = makeCastScope(caster, spell, effect, fight.map().get(123));
        assertThrows(UnsupportedOperationException.class, () -> handler.buff(scope, scope.effects().get(0)));
    }

    @Test
    void maxInvocReached() throws SQLException {
        PlayerFighter dead = makePlayerFighter(makeSimpleGamePlayer(40));
        dead.setTeam(player.fighter().team());
        fight.fighters().joinTurnList(dead, fight.map().get(200));
        dead.init();

        dead.life().kill(dead);

        Spell spell = Mockito.mock(Spell.class);
        PlayableFighter invoc = Mockito.mock(PlayableFighter.class);
        Mockito.when(invoc.invoker()).thenReturn(caster);
        fight.turnList().add(invoc);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Mockito.when(effect.effect()).thenReturn(180);
        Mockito.when(spell.effects()).thenReturn(Collections.singletonList(effect));

        assertFalse(handler.check(turn, spell, fight.map().get(123)));
        assertEquals(
            new Error(203, 1).toString(),
            handler.validate(turn, spell, fight.map().get(123)).toString()
        );
    }

    @Test
    void noDeadFighterYet() throws SQLException {
        Spell spell = Mockito.mock(Spell.class);
        PlayableFighter invoc = Mockito.mock(PlayableFighter.class);
        Mockito.when(invoc.invoker()).thenReturn(caster);
        fight.turnList().add(invoc);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Mockito.when(effect.effect()).thenReturn(180);
        Mockito.when(spell.effects()).thenReturn(Collections.singletonList(effect));

        assertFalse(handler.check(turn, spell, fight.map().get(123)));
        assertEquals(
            Error.cantCast().toString(),
            handler.validate(turn, spell, fight.map().get(123)).toString()
        );
    }

    @Test
    void maxInvocReachedWithMultipleInvoc() throws SQLException {
        PlayerFighter dead = makePlayerFighter(makeSimpleGamePlayer(40));
        dead.setTeam(player.fighter().team());
        fight.fighters().joinTurnList(dead, fight.map().get(200));
        dead.init();

        dead.life().kill(dead);

        Spell spell = Mockito.mock(Spell.class);
        PlayableFighter invoc = Mockito.mock(PlayableFighter.class);
        Mockito.when(invoc.invoker()).thenReturn(caster);

        // Add 4 invoc
        fight.turnList().add(invoc);
        fight.turnList().add(invoc);
        fight.turnList().add(invoc);
        fight.turnList().add(invoc);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Mockito.when(effect.effect()).thenReturn(180);
        Mockito.when(spell.effects()).thenReturn(Collections.singletonList(effect));

        caster.characteristics().alter(Characteristic.MAX_SUMMONED_CREATURES, 3);

        assertFalse(handler.check(turn, spell, fight.map().get(123)));
        assertEquals(
            new Error(203, 4).toString(),
            handler.validate(turn, spell, fight.map().get(123)).toString()
        );
    }

    @Test
    void shouldIgnoreStaticInvocation() throws SQLException {
        PlayerFighter dead = makePlayerFighter(makeSimpleGamePlayer(40));
        dead.setTeam(player.fighter().team());
        fight.fighters().joinTurnList(dead, fight.map().get(200));
        dead.init();

        dead.life().kill(dead);

        Spell spell = Mockito.mock(Spell.class);

        Fighter invoc1 = Mockito.mock(Fighter.class);
        Fighter invoc2 = Mockito.mock(Fighter.class);
        Fighter invoc3 = Mockito.mock(Fighter.class);
        Fighter invoc4 = Mockito.mock(Fighter.class);

        Mockito.when(invoc1.invoker()).thenReturn(caster);
        Mockito.when(invoc2.invoker()).thenReturn(caster);
        Mockito.when(invoc3.invoker()).thenReturn(caster);
        Mockito.when(invoc4.invoker()).thenReturn(caster);

        fight.fighters().join(invoc1, fight.map().get(146));
        fight.fighters().join(invoc2, fight.map().get(146));
        fight.fighters().join(invoc3, fight.map().get(146));
        fight.fighters().join(invoc4, fight.map().get(146));

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Mockito.when(effect.effect()).thenReturn(180);
        Mockito.when(spell.effects()).thenReturn(Collections.singletonList(effect));

        caster.characteristics().alter(Characteristic.MAX_SUMMONED_CREATURES, 3);

        assertTrue(handler.check(turn, spell, fight.map().get(123)));
    }

    @Test
    void success() throws SQLException {
        PlayerFighter dead = makePlayerFighter(makeSimpleGamePlayer(40));
        dead.setTeam(player.fighter().team());
        fight.fighters().joinTurnList(dead, fight.map().get(200));
        dead.init();

        dead.life().kill(dead);

        Spell spell = Mockito.mock(Spell.class);

        assertTrue(handler.check(turn, spell, fight.map().get(123)));
        assertNull(handler.validate(turn, spell, fight.map().get(123)));
    }

    @Test
    void successWithMultipleInvoc() throws SQLException {
        PlayerFighter dead = makePlayerFighter(makeSimpleGamePlayer(40));
        dead.setTeam(player.fighter().team());
        fight.fighters().joinTurnList(dead, fight.map().get(200));
        dead.init();

        dead.life().kill(dead);

        Spell spell = Mockito.mock(Spell.class);
        PlayableFighter invoc = Mockito.mock(PlayableFighter.class);
        Mockito.when(invoc.invoker()).thenReturn(caster);
        fight.turnList().add(invoc);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Mockito.when(effect.effect()).thenReturn(180);
        Mockito.when(spell.effects()).thenReturn(Collections.singletonList(effect));

        caster.characteristics().alter(Characteristic.MAX_SUMMONED_CREATURES, 3);

        assertTrue(handler.check(turn, spell, fight.map().get(123)));
        assertNull(handler.validate(turn, spell, fight.map().get(123)));
    }
}
