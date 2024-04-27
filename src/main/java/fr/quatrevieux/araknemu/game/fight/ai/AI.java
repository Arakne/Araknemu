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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai;

import fr.quatrevieux.araknemu.game.fight.ai.memory.MemoryKey;
import fr.quatrevieux.araknemu.game.fight.ai.util.AIHelper;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldMap;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Base type for AI
 * Use by action generators as data accessor
 */
public interface AI {
    /**
     * Get the fighter controlled by the AI
     */
    public ActiveFighter fighter();

    /**
     * Get the current fight map
     */
    public BattlefieldMap map();

    /**
     * Get the current turn
     */
    public Turn<?> turn();

    /**
     * Get all fighter of the current fight
     */
    public Stream<? extends FighterData> fighters();

    /**
     * Get the best enemy
     * This method behavior can change, depending on the AI resolution strategy
     *
     * An empty optional can be returned, if there is no enemy which match
     */
    public Optional<? extends FighterData> enemy();

    /**
     * Get the main ally
     * This method behavior can change, depending on the AI resolution strategy
     *
     * Unlike {@link #enemy()}, this method does not return a value most of the time,
     * it is used to mark a given ally as the main target for buffs, heals, etc.
     *
     * In case of invocation, this method should return the invoker.
     *
     * An empty optional can be returned, no ally is preferred
     */
    public default Optional<? extends FighterData> ally() {
        return Optional.empty();
    }

    /**
     * Get helper for the current AI
     */
    public default AIHelper helper() {
        return new AIHelper(this);
    }

    /**
     * Get a value from the memory
     * This call will forward to {@link fr.quatrevieux.araknemu.game.fight.ai.memory.AiMemory#get(MemoryKey)}
     *
     * @param key The key of the value. Should be a static instance of {@link MemoryKey}
     *
     * @return The stored value or the default value if the value is not present
     *
     * @param <T> The type of the value
     *
     * @see MemoryKey#defaultValue() Used when the value is not present
     */
    public <T> @Nullable T get(MemoryKey<T> key);

    /**
     * Set a value in the memory
     * This call will forward to {@link fr.quatrevieux.araknemu.game.fight.ai.memory.AiMemory#set(MemoryKey, Object)}
     *
     * @param key The key of the value. Should be a static instance of {@link MemoryKey}
     * @param value The value to store
     *
     * @param <T> The type of the value
     */
    public <T> void set(MemoryKey<T> key, @NonNull T value);
}
