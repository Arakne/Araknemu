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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.invocations;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.InvocationFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

import fr.quatrevieux.araknemu.network.game.out.fight.turn.FighterTurnOrder;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class MonsterInvocationHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private MonsterInvocationHandler handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMonsterTemplateInvocations().pushMonsterSpellsInvocations().pushRewardItems();

        fight = createFight();
        fight.nextState();

        caster = player.fighter();

        handler = new MonsterInvocationHandler(container.get(MonsterService.class), container.get(FighterFactory.class), fight);

        requestStack.clear();
    }

    @Test
    void handle() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.min()).thenReturn(36); // bouftou
        Mockito.when(effect.max()).thenReturn(1); // grade 1
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(true);

        CastScope scope = makeCastScope(caster, spell, effect, fight.map().get(123));
        handler.handle(scope, scope.effects().get(0));

        PassiveFighter invoc = fight.map().get(123).fighter().get();

        assertInstanceOf(InvocationFighter.class, invoc);
        assertContains(invoc, fight.fighters());
        assertContains(invoc, fight.turnList().fighters());
        assertSame(caster.team(), invoc.team());
        assertEquals(1, invoc.level());
        assertEquals(36, ((InvocationFighter) invoc).monster().id());

        requestStack.assertAll(
            new FighterTurnOrder(fight.turnList()),
            new ActionEffect(181, caster, "+" + invoc.sprite()),
            new ActionEffect(999, caster, (new FighterTurnOrder(fight.turnList())).toString())
        );
    }

    @Test
    void buff() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.min()).thenReturn(36); // bouftou
        Mockito.when(effect.max()).thenReturn(1); // grade 1
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(true);

        CastScope scope = makeCastScope(caster, spell, effect, fight.map().get(123));
        handler.buff(scope, scope.effects().get(0));

        PassiveFighter invoc = fight.map().get(123).fighter().get();

        assertInstanceOf(InvocationFighter.class, invoc);
        assertContains(invoc, fight.fighters());
        assertContains(invoc, fight.turnList().fighters());
        assertSame(caster.team(), invoc.team());
        assertEquals(1, invoc.level());
        assertEquals(36, ((InvocationFighter) invoc).monster().id());
    }
}
