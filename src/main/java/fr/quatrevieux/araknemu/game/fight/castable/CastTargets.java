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

package fr.quatrevieux.araknemu.game.fight.castable;

import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.util.NullnessUtil;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Store targets of a casted effect
 * It associates a target fighter with its cell
 *
 * The goal of this class is to minimize memory allocations, and provide an immutable "collection" of targets
 */
@SuppressWarnings({"accessing.nullable", "array.access.unsafe.high.range"})
public final class CastTargets<F extends FighterData> implements Iterable<F> {
    @SuppressWarnings("argument")
    private static final CastTargets<?> EMPTY = new CastTargets<>(null, null, null, null, 0);

    private final @Nullable F fighter;
    private final @Nullable BattlefieldCell cell;
    private final F @Nullable [] fighters;
    private final BattlefieldCell @Nullable [] cells;
    private final @NonNegative int size;

    private CastTargets(@Nullable F fighter, @Nullable BattlefieldCell cell, F @Nullable [] fighters, BattlefieldCell @Nullable [] cells, @NonNegative int size) {
        this.fighter = fighter;
        this.cell = cell;
        this.fighters = fighters;
        this.cells = cells;
        this.size = size;
    }

    @Override
    public Iterator<F> iterator() {
        switch (size) {
            case 0:
                return Collections.emptyIterator();

            case 1:
                return new SingletonIterator();

            default:
                return new ManyIterator();
        }
    }

    /**
     * Iterate over targets and cells
     *
     * @param consumer The consumer. Takes the fighter and its cell as arguments, and returns true if the iteration should continue, false otherwise
     */
    @SuppressWarnings("array.access.unsafe.high")
    public void forEach(TargetConsumer<F> consumer) {
        final @NonNegative int size = this.size;

        if (size == 0) {
            return;
        }

        if (size == 1) {
            consumer.accept(
                NullnessUtil.castNonNull(fighter),
                NullnessUtil.castNonNull(cell)
            );
            return;
        }

        final F[] fighters = NullnessUtil.castNonNull(this.fighters);
        final BattlefieldCell[] cells = NullnessUtil.castNonNull(this.cells);

        for (int i = 0; i < size; i++) {
            final boolean ok = consumer.accept(
                NullnessUtil.castNonNull(fighters[i]),
                NullnessUtil.castNonNull(cells[i])
            );

            if (!ok) {
                break;
            }
        }
    }

    /**
     * Get an empty target
     *
     * @return The empty instance.
     * @param <F> The fighter type
     */
    public static <F extends FighterData> CastTargets<F> empty() {
        return (CastTargets<F>) EMPTY;
    }

    /**
     * Create a singleton target
     *
     * @param fighter The target fighter
     * @param cell The target cell. This cell should not the fighter cell but the target cell
     *
     * @return The new instance
     *
     * @param <F> The fighter type
     */
    public static <F extends FighterData> CastTargets<F> one(F fighter, BattlefieldCell cell) {
        return new CastTargets<>(fighter, cell, (F[]) null, (BattlefieldCell[])  null, 1);
    }

    /**
     * Create a builder for multiple targets
     *
     * @return The builder instance
     *
     * @param <F> The fighter type
     */
    public static <F extends FighterData> Builder<F> builder() {
        return new Builder<>();
    }

    /**
     * Create with multiple targets
     *
     * @param fighters Array of fighters
     * @param cells Array of cells. Should be the same size as fighters
     * @param size The size of both arrays
     *
     * @return The new instance
     *
     * @param <F> The fighter type
     */
    private static <F extends FighterData> CastTargets<F> many(F[] fighters, BattlefieldCell[] cells, @NonNegative int size) {
        return new CastTargets<>(null, null, fighters, cells, size);
    }

    @FunctionalInterface
    public interface TargetConsumer<F extends FighterData> {
        public boolean accept(F fighter, BattlefieldCell cell);
    }

    /**
     * Builder for CastTargets
     *
     * @param <F> The fighter type
     */
    public static final class Builder<F extends FighterData> {
        private static final @Positive int INITIAL_CAPACITY = 4;

        private @Nullable F fighter = null;
        private @Nullable BattlefieldCell cell = null;
        private F @Nullable [] fighters = null;
        private BattlefieldCell @Nullable [] cells = null;
        private @NonNegative int size = 0;

        /**
         * Add a target
         *
         * @param fighter The target fighter.
         * @param cell The cell of the fighter.
         */
        public void add(F fighter, BattlefieldCell cell) {
            switch (size) {
                case 0:
                    singleton(fighter, cell);
                    break;

                case 1:
                    pair(fighter, cell);
                    break;

                default:
                    push(fighter, cell);
            }
        }

        /**
         * Build the CastTargets instance
         */
        public CastTargets<F> build() {
            switch (size) {
                case 0:
                    return CastTargets.empty();

                case 1:
                    return CastTargets.one(NullnessUtil.castNonNull(fighter), NullnessUtil.castNonNull(cell));

                default:
                    return CastTargets.many(NullnessUtil.castNonNull(fighters), NullnessUtil.castNonNull(cells), size);
            }
        }

        private void singleton(F fighter, BattlefieldCell cell) {
            this.fighter = fighter;
            this.cell = cell;
            size = 1;
        }

        @SuppressWarnings({"unchecked", "array.access.unsafe.high.constant", "array.access.unsafe.high"})
        private void pair(F fighter, BattlefieldCell cell) {
            fighters = (F[]) new FighterData[INITIAL_CAPACITY];
            cells = new BattlefieldCell[INITIAL_CAPACITY];

            fighters[0] = NullnessUtil.castNonNull(this.fighter);
            cells[0] = NullnessUtil.castNonNull(this.cell);

            fighters[1] = fighter;
            cells[1] = cell;

            size = 2;
        }

        @SuppressWarnings({"dereference.of.nullable", "argument", "unchecked", "array.access.unsafe.high"})
        private void push(F fighter, BattlefieldCell cell) {
            final int lastSize = size;

            if (lastSize == fighters.length) {
                final F[] newFighters = (F[]) new FighterData[lastSize * 2];
                final BattlefieldCell[] newCells = new BattlefieldCell[lastSize * 2];

                System.arraycopy(fighters, 0, newFighters, 0, lastSize);
                System.arraycopy(cells, 0, newCells, 0, lastSize);

                fighters = newFighters;
                cells = newCells;
            }

            fighters[lastSize] = fighter;
            cells[lastSize] = cell;

            size++;
        }
    }

    private final class ManyIterator implements Iterator<F> {
        private @NonNegative int index = 0;

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public F next() {
            if (index >= size) {
                throw new NoSuchElementException();
            }

            return NullnessUtil.castNonNull(fighters[index++]);
        }
    }

    private final class SingletonIterator implements Iterator<F> {
        private boolean hasNext = true;

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public F next() {
            if (!hasNext) {
                throw new NoSuchElementException();
            }

            hasNext = false;
            return NullnessUtil.castNonNull(fighter);
        }
    }
}
