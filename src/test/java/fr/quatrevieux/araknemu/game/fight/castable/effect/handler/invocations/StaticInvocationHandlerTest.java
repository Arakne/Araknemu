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
import fr.quatrevieux.araknemu.game.fight.ai.FighterAI;
import fr.quatrevieux.araknemu.game.fight.ai.factory.AiFactory;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.StaticInvocationFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.module.AiModule;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StaticInvocationHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private StaticInvocationHandler handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMonsterTemplateInvocations().pushMonsterSpellsInvocations().pushRewardItems();

        fight = createFight();
        fight.register(new AiModule(container.get(AiFactory.class)));
        fight.nextState();

        caster = player.fighter();

        handler = new StaticInvocationHandler(container.get(MonsterService.class), container.get(FighterFactory.class), fight);

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

        FightCastScope scope = makeCastScope(caster, spell, effect, fight.map().get(123));
        handler.handle(scope, scope.effects().get(0));

        Fighter invoc = fight.map().get(123).fighter();

        assertInstanceOf(StaticInvocationFighter.class, invoc);
        assertContains(invoc, fight.fighters().all());
        assertFalse(fight.turnList().fighters().contains(invoc));
        assertSame(caster.team(), invoc.team());
        assertEquals(1, invoc.level());
        assertEquals(36, ((StaticInvocationFighter) invoc).monster().id());

        requestStack.assertAll(
            new ActionEffect(185, caster, "+" + invoc.sprite())
        );

        assertNull(invoc.attachment(FighterAI.class));
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

        FightCastScope scope = makeCastScope(caster, spell, effect, fight.map().get(123));
        assertThrows(UnsupportedOperationException.class, () -> handler.buff(scope, scope.effects().get(0)));
    }
}
