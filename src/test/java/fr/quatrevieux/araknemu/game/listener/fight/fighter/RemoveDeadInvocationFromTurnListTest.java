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
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.InvocationFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.StaticInvocationFighter;
import fr.quatrevieux.araknemu.game.fight.module.MonsterInvocationModule;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RemoveDeadInvocationFromTurnListTest extends FightBaseCase {
    private MonsterService monsterService;
    private Fight fight;
    private RemoveDeadInvocationFromTurnList listener;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMonsterTemplateInvocations()
            .pushMonsterSpellsInvocations()
        ;

        fight = createFight(true);

        monsterService = container.get(MonsterService.class);
        listener = new RemoveDeadInvocationFromTurnList(fight);
    }

    @Test
    void onFighterDieInvocationShouldBeRemovedFromTurnList() throws Exception {
        fight.state(PlacementState.class).startFight();
        fight.turnList().start();

        InvocationFighter invoc = new InvocationFighter(-5, monsterService.load(36).get(2), player.fighter().team(), player.fighter());
        invoc.joinFight(fight, fight.map().get(123));
        fight.turnList().add(invoc);
        invoc.init();

        listener.on(new FighterDie(invoc, invoc));

        assertFalse(fight.turnList().fighters().contains(invoc));
    }

    @Test
    void onFighterDieNotInvocationShouldBeIgnored() throws Exception {
        fight.state(PlacementState.class).startFight();
        fight.turnList().start();

        listener.on(new FighterDie(player.fighter(), player.fighter()));

        assertTrue(fight.turnList().fighters().contains(player.fighter()));
    }

    @Test
    void onFighterDieStaticInvocationShouldBeIgnored() throws Exception {
        fight.state(PlacementState.class).startFight();
        fight.turnList().start();

        StaticInvocationFighter invoc = new StaticInvocationFighter(-5, monsterService.load(36).get(2), player.fighter().team(), player.fighter());
        invoc.joinFight(fight, fight.map().get(123));
        invoc.init();

        listener.on(new FighterDie(invoc, invoc));
    }
}
