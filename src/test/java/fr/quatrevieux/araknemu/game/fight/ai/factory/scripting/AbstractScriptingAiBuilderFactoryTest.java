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

package fr.quatrevieux.araknemu.game.fight.ai.factory.scripting;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.FighterAI;
import fr.quatrevieux.araknemu.game.fight.ai.action.ActionGenerator;
import fr.quatrevieux.araknemu.game.fight.ai.action.AiActionFactory;
import fr.quatrevieux.araknemu.game.fight.ai.action.Attack;
import fr.quatrevieux.araknemu.game.fight.ai.action.Heal;
import fr.quatrevieux.araknemu.game.fight.ai.action.MoveFarEnemies;
import fr.quatrevieux.araknemu.game.fight.ai.action.MoveNearEnemy;
import fr.quatrevieux.araknemu.game.fight.ai.action.MoveToAttack;
import fr.quatrevieux.araknemu.game.fight.ai.action.logic.ConditionalGenerator;
import fr.quatrevieux.araknemu.game.fight.ai.action.logic.GeneratorAggregate;
import fr.quatrevieux.araknemu.game.fight.ai.factory.AggregateAiFactory;
import fr.quatrevieux.araknemu.game.fight.ai.memory.MemoryKey;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractScriptingAiBuilderFactoryTest extends FightBaseCase {
    public static final MemoryKey<String> SHARED_KEY = new MemoryKey<String>() {};

    private AggregateAiFactory<ActiveFighter> factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        factory = new AggregateAiFactory<>(Collections.singleton(
            new ScriptingAiLoader<>(Paths.get("src/test/scripts/ai/asabf"), container.instantiator(), Mockito.mock(Logger.class), false)
        ), f -> null);
        Fight fight = createFight(true);
        fight.nextState();
    }

    @Test
    void basic() throws NoSuchFieldException, IllegalAccessException {
        AI ai = factory.create(player.fighter(), "basic").get();

        assertActions(ai, MoveToAttack.class, Attack.class, MoveNearEnemy.class, Heal.class);
    }

    @Test
    void conditional() throws NoSuchFieldException, IllegalAccessException {
        AI ai = factory.create(player.fighter(), "condition").get();

        assertInstanceOf(ConditionalGenerator.class, extractGenerator(ai));
        assertConditional((ConditionalGenerator) extractGenerator(ai),
            c -> {
                assertTrue(c.test(ai));
                player.fighter().life().damage(player.fighter(), 200);
                assertFalse(c.test(ai));
            },
            new Class[]{MoveToAttack.class, Attack.class, MoveNearEnemy.class},
            new Class[]{Heal.class, Attack.class, MoveToAttack.class, MoveFarEnemies.class}
        );
    }

    @Test
    void addInlineAction() throws NoSuchFieldException, IllegalAccessException {
        AI ai = factory.create(player.fighter(), "inline").get();

        ActionGenerator generator = extractGenerator(ai);

        assertFalse(generator.generate(ai, Mockito.mock(AiActionFactory.class)).isPresent());
        assertEquals("Hello, World!", ai.get(SHARED_KEY));
    }

    @Test
    void missingImplementation() {
        assertThrows(UnsupportedOperationException.class, () -> factory.create(player.fighter(), "missing"));
    }

    private ActionGenerator extractGenerator(AI ai) throws NoSuchFieldException, IllegalAccessException {
        Field field = FighterAI.class.getDeclaredField("generator");
        field.setAccessible(true);

        return (ActionGenerator) field.get(ai);
    }

    private void assertActions(AI ai, Class<? extends ActionGenerator> ...types) throws NoSuchFieldException, IllegalAccessException {
        assertActions((GeneratorAggregate) extractGenerator(ai), types);
    }

    private void assertActions(GeneratorAggregate actions, Class<? extends ActionGenerator> ...types) throws NoSuchFieldException, IllegalAccessException {
        Field actionsField = GeneratorAggregate.class.getDeclaredField("generators");
        actionsField.setAccessible(true);

        ActionGenerator[] actual = (ActionGenerator[]) actionsField.get(actions);

        for (int i = 0; i < types.length; ++i) {
            assertInstanceOf(types[i], actual[i]);
        }
    }

    private void assertConditional(ConditionalGenerator generator, Consumer<Predicate<AI>> predicateAsserter, Class[] successActions, Class[] otherwiseActions) throws NoSuchFieldException, IllegalAccessException {
        Field conditionField = ConditionalGenerator.class.getDeclaredField("condition");
        Field successField = ConditionalGenerator.class.getDeclaredField("success");
        Field otherwiseField = ConditionalGenerator.class.getDeclaredField("otherwise");

        conditionField.setAccessible(true);
        successField.setAccessible(true);
        otherwiseField.setAccessible(true);

        predicateAsserter.accept((Predicate<AI>) conditionField.get(generator));
        assertActions((GeneratorAggregate) successField.get(generator), successActions);
        assertActions((GeneratorAggregate) otherwiseField.get(generator), otherwiseActions);
    }
}
