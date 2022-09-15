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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.module;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.InvocationFighter;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MonsterInvocationModuleTest extends FightBaseCase {
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

        fight.register(new MonsterInvocationModule(monsterService, container.get(FighterFactory.class), fight));
        fight.state(PlacementState.class).startFight();
        fight.turnList().start();

        InvocationFighter invoc1 = new InvocationFighter(-5, monsterService.load(36).get(2), player.fighter().team(), player.fighter());
        InvocationFighter invoc2 = new InvocationFighter(-6, monsterService.load(36).get(2), player.fighter().team(), player.fighter());
        InvocationFighter invoc3 = new InvocationFighter(-7, monsterService.load(36).get(4), other.fighter().team(), other.fighter());

        invoc1.joinFight(fight, fight.map().get(123));
        invoc2.joinFight(fight, fight.map().get(124));
        invoc3.joinFight(fight, fight.map().get(125));

        fight.turnList().add(invoc1);
        fight.turnList().add(invoc2);
        fight.turnList().add(invoc3);

        invoc1.init();
        invoc2.init();
        invoc3.init();

        player.fighter().life().kill(player.fighter());

        assertTrue(invoc1.dead());
        assertTrue(invoc2.dead());
        assertFalse(fight.map().get(123).fighter().isPresent());
        assertFalse(fight.map().get(124).fighter().isPresent());
        assertFalse(fight.turnList().fighters().contains(invoc1));
        assertFalse(fight.turnList().fighters().contains(invoc2));
        assertFalse(fight.fighters().contains(invoc1));
        assertFalse(fight.fighters().contains(invoc2));

        // Do not change invocation of other fighter
        assertFalse(invoc3.dead());
        assertTrue(fight.map().get(125).fighter().isPresent());
        assertTrue(fight.turnList().fighters().contains(invoc3));
        assertTrue(fight.fighters().contains(invoc3));
    }

    @Test
    void onFighterDieInvocationShouldBeRemovedFromTurnList() throws Exception {
        Fight fight = createFight(true);

        fight.register(new MonsterInvocationModule(monsterService, container.get(FighterFactory.class), fight));
        fight.state(PlacementState.class).startFight();
        fight.turnList().start();

        InvocationFighter invoc = new InvocationFighter(-5, monsterService.load(36).get(2), player.fighter().team(), player.fighter());
        invoc.joinFight(fight, fight.map().get(123));
        fight.turnList().add(invoc);
        invoc.init();

        invoc.life().kill(player.fighter());

        assertTrue(invoc.dead());
        assertFalse(fight.map().get(123).fighter().isPresent());
        assertFalse(fight.turnList().fighters().contains(invoc));
        assertFalse(fight.fighters().contains(invoc));
    }

    @Test
    void onFighterDieWithEndFightShouldNotCauseConcurrentModification() throws Exception {
        Fight fight = createFight(true);

        fight.register(new MonsterInvocationModule(monsterService, container.get(FighterFactory.class), fight));
        fight.state(PlacementState.class).startFight();
        fight.turnList().start();

        InvocationFighter invoc = new InvocationFighter(-5, monsterService.load(36).get(2), player.fighter().team(), player.fighter());
        invoc.joinFight(fight, fight.map().get(123));
        fight.turnList().add(invoc);
        invoc.init();

        player.fighter().life().kill(player.fighter());
    }
}
