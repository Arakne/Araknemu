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

import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.FighterAI;
import fr.quatrevieux.araknemu.game.fight.ai.action.logic.NullGenerator;
import fr.quatrevieux.araknemu.game.fight.ai.memory.MemoryKey;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AggregateAiFactoryTest extends FightBaseCase {
    private static final MemoryKey<String> AI_TYPE = new MemoryKey<String>() {};

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        createFight();
    }

    @Test
    void createWithName() {
        AggregateAiFactory factory = new AggregateAiFactory(
            Collections.singletonList(new ListAiFactoryLoader<>(new Foo(), new Bar())),
            fighter -> null
        );

        Optional<AI> ai = factory.create(player.fighter(), "foo");
        assertTrue(ai.isPresent());
        assertEquals("foo", ai.get().get(AI_TYPE));

        ai = factory.create(player.fighter(), "bar");
        assertTrue(ai.isPresent());
        assertEquals("bar", ai.get().get(AI_TYPE));

        assertThrows(IllegalArgumentException.class, () -> factory.create(player.fighter(), "baz"));
    }

    @Test
    void createWithoutName() {
        AggregateAiFactory factory = new AggregateAiFactory(
            Collections.singletonList(new ListAiFactoryLoader<>(new Foo(), new Bar())),
            fighter -> {
                if (fighter.equals(player.fighter())) {
                    return "foo";
                } else {
                    return "bar";
                }
            }
        );

        Optional<AI> ai = factory.create(player.fighter());
        assertTrue(ai.isPresent());
        assertEquals("foo", ai.get().get(AI_TYPE));

        ai = factory.create(other.fighter());
        assertTrue(ai.isPresent());
        assertEquals("bar", ai.get().get(AI_TYPE));
    }

    @Test
    void createNotAiResolved() {
        AggregateAiFactory factory = new AggregateAiFactory(
            Collections.singletonList(new ListAiFactoryLoader<>(new Foo(), new Bar())),
            fighter -> null
        );

        Optional<AI> ai = factory.create(player.fighter());
        assertFalse(ai.isPresent());
    }

    @Test
    void lazyLoading() {
        AiFactoryLoader loader1 = Mockito.mock(AiFactoryLoader.class);
        Mockito.when(loader1.lazy()).thenReturn(true);
        Mockito.when(loader1.load()).thenReturn(Collections.singletonList(new Foo()));

        AiFactoryLoader loader2 = Mockito.mock(AiFactoryLoader.class);
        Mockito.when(loader2.lazy()).thenReturn(true);
        Mockito.when(loader2.load()).thenReturn(Collections.singletonList(new Bar()));

        AggregateAiFactory factory = new AggregateAiFactory(
            Arrays.asList(loader1, loader2),
            fighter -> null
        );
        Mockito.verify(loader1, Mockito.times(1)).lazy();
        Mockito.verify(loader2, Mockito.times(1)).lazy();
        Mockito.verify(loader1, Mockito.never()).load();
        Mockito.verify(loader2, Mockito.never()).load();

        Optional<AI> ai = factory.create(player.fighter(), "foo");
        assertTrue(ai.isPresent());
        assertEquals("foo", ai.get().get(AI_TYPE));
        Mockito.verify(loader1, Mockito.times(1)).load();
        Mockito.verify(loader2, Mockito.never()).load();
        Mockito.clearInvocations(loader1, loader2);

        ai = factory.create(other.fighter(), "bar");
        assertTrue(ai.isPresent());
        assertEquals("bar", ai.get().get(AI_TYPE));
        Mockito.verify(loader1, Mockito.times(1)).load();
        Mockito.verify(loader2, Mockito.times(1)).load();
        Mockito.clearInvocations(loader1, loader2);

        ai = factory.create(other.fighter(), "bar");
        assertTrue(ai.isPresent());
        assertEquals("bar", ai.get().get(AI_TYPE));
        Mockito.verify(loader1, Mockito.never()).load();
        Mockito.verify(loader2, Mockito.never()).load();
    }

    class Foo implements NamedAiFactory<PlayableFighter> {
        @Override
        public Optional<AI> create(PlayableFighter fighter) {
            final FighterAI ai = new FighterAI(fighter, fighter.fight(), new NullGenerator());

            ai.set(AI_TYPE, "foo");

            return Optional.of(ai);
        }
    }

    class Bar implements NamedAiFactory<PlayableFighter> {
        @Override
        public Optional<AI> create(PlayableFighter fighter) {
            final FighterAI ai = new FighterAI(fighter, fighter.fight(), new NullGenerator());

            ai.set(AI_TYPE, "bar");

            return Optional.of(ai);
        }
    }
}
