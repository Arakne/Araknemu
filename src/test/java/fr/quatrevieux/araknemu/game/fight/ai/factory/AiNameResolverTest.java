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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.factory;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.InvocationFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AiNameResolverTest extends FightBaseCase {
    @Test
    void withGamePlayer() throws Exception {
        createFight();

        assertNull(new AiNameResolver().apply(player.fighter()));
    }

    @Test
    void withMonster() throws Exception {
        Fight fight = createPvmFight();
        dataSet
            .pushMonsterTemplateInvocations()
            .pushMonsterSpellsInvocations()
        ;

        MonsterFighter fighter = new MonsterFighter(-10, container.get(MonsterService.class).load(36).get(5), fight.team(1));
        fight.fighters().join(fighter, fight.map().get(144));
        assertEquals("AGGRESSIVE", new AiNameResolver().apply(fighter));

        fighter = new MonsterFighter(-11, container.get(MonsterService.class).load(44).get(5), fight.team(1));
        fight.fighters().join(fighter, fight.map().get(143));
        assertEquals("RUNAWAY", new AiNameResolver().apply(fighter));
    }

    @Test
    void withInvocation() throws Exception {
        Fight fight = createPvmFight();
        dataSet
            .pushMonsterTemplateInvocations()
            .pushMonsterSpellsInvocations()
        ;

        InvocationFighter fighter = new InvocationFighter(
            -10,
            container.get(MonsterService.class).load(36).get(5),
            fight.team(1),
            player.fighter()
        );
        fight.fighters().join(fighter, fight.map().get(144));
        assertEquals("AGGRESSIVE", new AiNameResolver().apply(fighter));

        fighter = new InvocationFighter(
            -11,
            container.get(MonsterService.class).load(44).get(5),
            fight.team(1),
            player.fighter()
        );
        fight.fighters().join(fighter, fight.map().get(143));
        assertEquals("RUNAWAY", new AiNameResolver().apply(fighter));
    }
}
