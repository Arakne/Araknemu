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
import fr.quatrevieux.araknemu.game.fight.ai.action.MoveFarEnemies;
import fr.quatrevieux.araknemu.game.fight.ai.action.MoveNearEnemy;
import fr.quatrevieux.araknemu.game.fight.ai.action.logic.ConditionalGenerator;
import fr.quatrevieux.araknemu.game.fight.ai.action.logic.GeneratorAggregate;
import fr.quatrevieux.araknemu.game.fight.ai.action.logic.NullGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertSame;

class ConditionalBuilderTest extends TestCase {
    private ConditionalBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new ConditionalBuilder(ai -> true);
    }

    @Test
    void buildEmpty() throws NoSuchFieldException, IllegalAccessException {
        assertGenerated(builder.build(), NullGenerator.get(), NullGenerator.get());
    }

    @Test
    void onlySuccess() throws NoSuchFieldException, IllegalAccessException {
        ActionGenerator g = Mockito.mock(ActionGenerator.class);

        assertGenerated(builder.success(g).build(), g, NullGenerator.get());
    }

    @Test
    void onlyOtherwise() throws NoSuchFieldException, IllegalAccessException {
        ActionGenerator g = Mockito.mock(ActionGenerator.class);

        assertGenerated(builder.otherwise(g).build(), NullGenerator.get(), g);
    }

    @Test
    void both() throws NoSuchFieldException, IllegalAccessException {
        ActionGenerator gs = Mockito.mock(ActionGenerator.class);
        ActionGenerator go = Mockito.mock(ActionGenerator.class);

        assertGenerated(builder.success(gs).otherwise(go).build(), gs, go);
    }

    @Test
    void usingMethodReference() throws NoSuchFieldException, IllegalAccessException {
        assertGenerated(
            builder.success(GeneratorBuilder::moveNearEnemy).otherwise(GeneratorBuilder::moveFarEnemies).build(),
            MoveNearEnemy.class,
            MoveFarEnemies.class
        );
    }

    @Test
    void multipleActionShouldGenerateAggregate() throws NoSuchFieldException, IllegalAccessException {
        ActionGenerator gs1 = Mockito.mock(ActionGenerator.class);
        ActionGenerator gs2 = Mockito.mock(ActionGenerator.class);
        ActionGenerator go1 = Mockito.mock(ActionGenerator.class);
        ActionGenerator go2 = Mockito.mock(ActionGenerator.class);

        assertGenerated(
            builder
                .success(gs1).success(gs2)
                .otherwise(go1).otherwise(go2)
                .build(),
            GeneratorAggregate.class,
            GeneratorAggregate.class
        );
    }

    private void assertGenerated(ActionGenerator generator, Class successType, Class otherwiseType) throws NoSuchFieldException, IllegalAccessException {
        assertInstanceOf(ConditionalGenerator.class, generator);

        Field success = ConditionalGenerator.class.getDeclaredField("success");
        Field otherwise = ConditionalGenerator.class.getDeclaredField("otherwise");
        success.setAccessible(true);
        otherwise.setAccessible(true);

        assertInstanceOf(successType, success.get(generator));
        assertInstanceOf(otherwiseType, otherwise.get(generator));
    }

    private void assertGenerated(ActionGenerator generator, ActionGenerator successAction, ActionGenerator otherwiseAction) throws NoSuchFieldException, IllegalAccessException {
        assertInstanceOf(ConditionalGenerator.class, generator);

        Field success = ConditionalGenerator.class.getDeclaredField("success");
        Field otherwise = ConditionalGenerator.class.getDeclaredField("otherwise");
        success.setAccessible(true);
        otherwise.setAccessible(true);

        assertSame(successAction, success.get(generator));
        assertSame(otherwiseAction, otherwise.get(generator));
    }
}
