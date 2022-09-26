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

package fr.quatrevieux.araknemu.game.fight.ai.proxy;

import fr.arakne.utils.maps.CoordinateCell;
import fr.arakne.utils.value.Dimensions;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldMap;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import org.checkerframework.checker.index.qual.IndexFor;
import org.checkerframework.checker.index.qual.LengthOf;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.SameLen;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Proxy class for redefine fighters and objects positions
 *
 * Note: this class is immutable
 */
public final class ProxyBattlefield implements BattlefieldMap {
    private final BattlefieldMap map;
    private final ProxyCell @Nullable @SameLen("this") [] cells;

    public ProxyBattlefield(BattlefieldMap map) {
        this.map = map;
        this.cells = null;
    }

    @EnsuresNonNull("cells")
    @SuppressWarnings({"assignment", "array.access.unsafe.high"}) // other and this have same length
    private ProxyBattlefield(ProxyBattlefield other) {
        this.map = other.map;
        this.cells = new ProxyCell[other.map.size()];

        for (int i = 0; i < map.size(); ++i) {
            this.cells[i] = other.cells == null
                ? new ProxyCell(map.get(i))
                : new ProxyCell(other.cells[i])
            ;
        }
    }

    @Override
    @SuppressWarnings("return") // map and this have same length
    public @LengthOf("this") int size() {
        return map.size();
    }

    @Override
    @SuppressWarnings({"argument"}) // map and this have same length
    public FightCell get(@IndexFor("this") int id) {
        if (cells == null) {
            return map.get(id);
        }

        return cells[id];
    }

    @Override
    public Dimensions dimensions() {
        return map.dimensions();
    }

    @Override
    public Iterator<FightCell> iterator() {
        if (cells == null) {
            return map.iterator();
        }

        return Arrays.<FightCell>asList(cells).iterator();
    }

    /**
     * Modify the map objects
     *
     * Usage:
     * <pre>{@code
     * map.modify(modifier -> {
     *     modifier.free(123).setFighter(125, fighter); // Move "fighter" to cell 125
     * });
     * }</pre>
     *
     * @param configurator Configuration callback
     *
     * @return The new map instance
     */
    public ProxyBattlefield modify(Consumer<Modifier> configurator) {
        final Modifier modifier = new Modifier(this);

        configurator.accept(modifier);

        return modifier.map;
    }

    private final class ProxyCell implements FightCell {
        private final FightCell cell;
        private boolean free = false;
        private @Nullable FighterData fighter = null;
        private @MonotonicNonNull CoordinateCell<FightCell> coordinates = null;

        private ProxyCell(FightCell cell) {
            this.cell = cell;
        }

        private ProxyCell(ProxyCell other) {
            this.cell = other.cell;
            this.free = other.free;
            this.fighter = other.fighter;
        }

        @Override
        public BattlefieldMap map() {
            return ProxyBattlefield.this;
        }

        @Override
        public boolean walkableIgnoreFighter() {
            return cell.walkableIgnoreFighter();
        }

        @Override
        public Optional<FighterData> fighter() {
            if (free) {
                return Optional.empty();
            }

            if (fighter != null) {
                return Optional.of(fighter);
            }

            return cell.fighter();
        }

        @Override
        public void set(FighterData fighter) {
            throw new UnsupportedOperationException("This is a proxy cell");
        }

        @Override
        public void removeFighter() {
            throw new UnsupportedOperationException("This is a proxy cell");
        }

        @Override
        public boolean sightBlocking() {
            if (free && cell.walkableIgnoreFighter()) {
                return false;
            }

            if (fighter != null) {
                return true;
            }

            return cell.sightBlocking();
        }

        @Override
        public @NonNegative int id() {
            return cell.id();
        }

        @Override
        public boolean walkable() {
            if (free) {
                return cell.walkableIgnoreFighter();
            }

            if (fighter != null) {
                return false;
            }

            return cell.walkable();
        }

        @Override
        public CoordinateCell<FightCell> coordinate() {
            if (coordinates == null) {
                coordinates = new CoordinateCell<>(this);
            }

            return coordinates;
        }
    }

    /**
     * Builder class for modifying a proxy map
     */
    @SuppressWarnings({"accessing.nullable", "array.access.unsafe.high"}) // map.cells is never null, but no way to tell this to checker
    public static class Modifier {
        private final ProxyBattlefield map;

        public Modifier(ProxyBattlefield map) {
            this.map = new ProxyBattlefield(map);
        }

        /**
         * Free a cell
         * Calling this method will remove the fighter onb this cell
         *
         * @param cellId Cell to modify
         *
         * @return this instance
         */
        public Modifier free(@NonNegative int cellId) {
            map.cells[cellId].free = true;

            return this;
        }

        /**
         * Get a cell instance
         *
         * @param cellId The cell to get
         */
        public FightCell get(@NonNegative int cellId) {
            return map.cells[cellId];
        }

        /**
         * Set a fighter to the given cell
         *
         * @param cellId Cell to modify
         * @param fighter The fighter to cell
         *
         * @return this instance
         */
        public Modifier setFighter(@NonNegative int cellId, FighterData fighter) {
            map.cells[cellId].free = false;
            map.cells[cellId].fighter = fighter;

            return this;
        }
    }
}
