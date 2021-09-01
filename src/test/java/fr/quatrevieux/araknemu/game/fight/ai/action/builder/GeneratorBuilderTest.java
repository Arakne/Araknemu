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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.action.builder;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.game.fight.ai.action.ActionGenerator;
import fr.quatrevieux.araknemu.game.fight.ai.action.Attack;
import fr.quatrevieux.araknemu.game.fight.ai.action.Boost;
import fr.quatrevieux.araknemu.game.fight.ai.action.MoveFarEnemies;
import fr.quatrevieux.araknemu.game.fight.ai.action.MoveNearAllies;
import fr.quatrevieux.araknemu.game.fight.ai.action.MoveNearEnemy;
import fr.quatrevieux.araknemu.game.fight.ai.action.MoveToAttack;
import fr.quatrevieux.araknemu.game.fight.ai.action.TeleportNearEnemy;
import fr.quatrevieux.araknemu.game.fight.ai.action.logic.ConditionalGenerator;
import fr.quatrevieux.araknemu.game.fight.ai.action.logic.GeneratorAggregate;
import fr.quatrevieux.araknemu.game.fight.ai.action.logic.NullGenerator;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class GeneratorBuilderTest extends TestCase {
    private GeneratorBuilder builder;
    private Simulator simulator;

    @BeforeEach
    void setUp() {
        builder = new GeneratorBuilder();
        simulator = new Simulator();
    }

    @Test
    void buildEmpty() {
        assertSame(NullGenerator.INSTANCE, builder.build());
    }

    @Test
    void buildSingleAction() {
        ActionGenerator generator = Mockito.mock(ActionGenerator.class);

        assertSame(generator, builder.add(generator).build());
    }

    @Test
    void buildMultiple() throws NoSuchFieldException, IllegalAccessException {
        ActionGenerator g1 = Mockito.mock(ActionGenerator.class);
        ActionGenerator g2 = Mockito.mock(ActionGenerator.class);
        ActionGenerator g3 = Mockito.mock(ActionGenerator.class);

        ActionGenerator built = builder.add(g1).add(g2).add(g3).build();

        assertInstanceOf(GeneratorAggregate.class, built);

        Field actions = GeneratorAggregate.class.getDeclaredField("actions");
        actions.setAccessible(true);

        assertArrayEquals(new ActionGenerator[] {g1, g2, g3}, (Object[]) actions.get(built));
    }

    @Test
    void when() throws NoSuchFieldException, IllegalAccessException {
        ActionGenerator gs = Mockito.mock(ActionGenerator.class);
        ActionGenerator go = Mockito.mock(ActionGenerator.class);

        ActionGenerator built = builder.when(ai -> true, cb -> cb.success(gs).otherwise(go)).build();

        assertInstanceOf(ConditionalGenerator.class, built);

        Field success = ConditionalGenerator.class.getDeclaredField("success");
        Field otherwise = ConditionalGenerator.class.getDeclaredField("otherwise");
        success.setAccessible(true);
        otherwise.setAccessible(true);

        assertSame(gs, success.get(built));
        assertSame(go, otherwise.get(built));
    }

    @Test
    void attackFromBestCell() throws NoSuchFieldException, IllegalAccessException {
        assertActions(builder.attackFromBestCell(simulator).build(), MoveToAttack.class, Attack.class);
    }

    @Test
    void attackFromNearestCell() throws NoSuchFieldException, IllegalAccessException {
        assertActions(builder.attackFromNearestCell(simulator).build(), Attack.class, MoveToAttack.class);
    }

    @Test
    void moveToAttack() {
        assertInstanceOf(MoveToAttack.class, builder.moveToAttack(simulator).build());
    }

    @Test
    void attack() {
        assertInstanceOf(Attack.class, builder.attack(simulator).build());
    }

    @Test
    void boostSelf() {
        assertInstanceOf(Boost.class, builder.boostSelf(simulator).build());
    }

    @Test
    void boostAllies() {
        assertInstanceOf(Boost.class, builder.boostAllies(simulator).build());
    }

    @Test
    void moveNearEnemy() {
        assertInstanceOf(MoveNearEnemy.class, builder.moveNearEnemy().build());
    }

    @Test
    void teleportNearEnemy() {
        assertInstanceOf(TeleportNearEnemy.class, builder.teleportNearEnemy().build());
    }

    @Test
    void moveOrTeleportNearEnemy() throws NoSuchFieldException, IllegalAccessException {
        assertActions(builder.moveOrTeleportNearEnemy().build(), MoveNearEnemy.class, TeleportNearEnemy.class);
    }

    @Test
    void moveFarEnemies() {
        assertInstanceOf(MoveFarEnemies.class, builder.moveFarEnemies().build());
    }

    @Test
    void moveNearAllies() {
        assertInstanceOf(MoveNearAllies.class, builder.moveNearAllies().build());
    }

    private void assertActions(ActionGenerator action, Class<? extends ActionGenerator> ...types) throws NoSuchFieldException, IllegalAccessException {
        assertInstanceOf(GeneratorAggregate.class, action);

        Field actions = GeneratorAggregate.class.getDeclaredField("actions");
        actions.setAccessible(true);

        ActionGenerator[] actual = (ActionGenerator[]) actions.get(action);

        for (int i = 0; i < types.length; ++i) {
            assertInstanceOf(types[i], actual[i]);
        }
    }
}
