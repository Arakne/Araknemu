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

package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.InvocationFighter;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KillInvocationsOnInvokerDieTest extends FightBaseCase {
    private MonsterService monsterService;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMonsterTemplateInvocations()
            .pushMonsterSpellsInvocations()
        ;

        monsterService = container.get(MonsterService.class);
    }

    @Test
    void onFighterDieShouldKillAllItsInvocation() throws Exception {
        FightBuilder builder = fightBuilder();
        builder
            .addSelf(fb -> fb.cell(222))
            .addAlly(fb -> fb.player(other).cell(223))
            .addEnemy(fb -> fb.cell(224))
        ;

        Fight fight = builder.build(true);

        fight.state(PlacementState.class).startFight();
        fight.turnList().start();

        KillInvocationsOnInvokerDie listener = new KillInvocationsOnInvokerDie(fight);

        InvocationFighter invoc1 = new InvocationFighter(-5, monsterService.load(36).get(2), player.fighter().team(), player.fighter());
        InvocationFighter invoc2 = new InvocationFighter(-6, monsterService.load(36).get(2), player.fighter().team(), player.fighter());
        InvocationFighter invoc3 = new InvocationFighter(-7, monsterService.load(36).get(4), other.fighter().team(), other.fighter());

        fight.fighters().joinTurnList(invoc1, fight.map().get(123));
        fight.fighters().joinTurnList(invoc2, fight.map().get(124));
        fight.fighters().joinTurnList(invoc3, fight.map().get(125));

        invoc1.init();
        invoc2.init();
        invoc3.init();

        listener.on(new FighterDie(player.fighter(), player.fighter()));

        assertTrue(invoc1.dead());
        assertTrue(invoc2.dead());
        assertFalse(fight.map().get(123).hasFighter());
        assertFalse(fight.map().get(124).hasFighter());
        assertFalse(fight.turnList().fighters().contains(invoc1));
        assertFalse(fight.turnList().fighters().contains(invoc2));
        assertTrue(fight.fighters().all().contains(invoc1));
        assertTrue(fight.fighters().all().contains(invoc2));

        // Do not change invocation of other fighter
        assertFalse(invoc3.dead());
        assertTrue(fight.map().get(125).hasFighter());
        assertTrue(fight.turnList().fighters().contains(invoc3));
        assertTrue(fight.fighters().all().contains(invoc3));
    }
}
