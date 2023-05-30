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

package fr.quatrevieux.araknemu.game.fight.map;

import fr.arakne.utils.maps.DofusMap;
import fr.arakne.utils.maps.path.Decoder;
import fr.arakne.utils.maps.serializer.CellData;
import fr.arakne.utils.value.Dimensions;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import org.checkerframework.checker.index.qual.IndexFor;
import org.checkerframework.checker.index.qual.LengthOf;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.SameLen;
import org.checkerframework.dataflow.qual.Pure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Map for the fight
 */
public final class FightMap implements BattlefieldMap {
    private final MapTemplate template;
    private final FightCell @SameLen("this") [] cells;
    private final Decoder<FightCell> decoder;
    private final BattlefieldObjects objects = new BattlefieldObjects();

    @SuppressWarnings({"argument", "method.invocation"}) // Do not resolve SameLen from template.cells()
    public FightMap(MapTemplate template) {
        this.template = template;
        this.cells = makeCells(this, template.cells());
        this.decoder = createDecoder();
    }

    /**
     * Get the map id
     */
    public @NonNegative int id() {
        return template.id();
    }

    /**
     * Get a fight cell
     *
     * @param cellId The cell id
     */
    @Override
    public FightCell get(@NonNegative @IndexFor("this") int cellId) {
        return cells[cellId];
    }

    /**
     * Get start places for a team
     */
    @SuppressWarnings("assignment") // fightPlaces bounds are not checked
    public List<FightCell> startPlaces(@NonNegative int team) {
        final @IndexFor("this") int[][] places = template.fightPlaces();

        if (team >= places.length) {
            return Collections.emptyList();
        }

        final List<FightCell> startCells = new ArrayList<>(places.length);

        for (int cellId : places[team]) {
            startCells.add(cells[cellId]);
        }

        return startCells;
    }

    @Pure
    @Override
    public Dimensions dimensions() {
        return template.dimensions();
    }

    @Pure
    @Override
    public @LengthOf("this") int size() {
        return cells.length;
    }

    @Override
    public Iterator<BattlefieldCell> iterator() {
        return Arrays.<BattlefieldCell>stream(cells).iterator();
    }

    /**
     * Get related cell decoder
     */
    public Decoder<FightCell> decoder() {
        return decoder;
    }

    /**
     * Clear map data
     */
    public void destroy() {
        for (FightCell cell : cells) {
            if (cell.hasFighter()) {
                cell.removeFighter();
            }
        }
    }

    private Decoder<FightCell> createDecoder() {
        // Because FightMap can't implement DofusMap<FightCell>
        // (java do not authorize two implementations of same interface with different generics parameters)
        // an adapter is created as Decoder parameter
        return new Decoder<>(new DofusMap<FightCell>() {
            @Override
            public @LengthOf({"this"}) int size() {
                return FightMap.this.size();
            }

            @Override
            public FightCell get(@IndexFor({"this"}) int id) {
                return FightMap.this.get(id);
            }

            @Override
            public Dimensions dimensions() {
                return FightMap.this.dimensions();
            }
        });
    }

    private static FightCell @SameLen("#1") [] makeCells(FightMap map, CellData @SameLen("#1") [] template) {
        final FightCell[] cells = new FightCell[template.length];

        for (int i = 0; i < template.length; ++i) {
            final CellData cell = template[i];

            if (!cell.active() || !cell.movement().walkable()) {
                cells[i] = new UnwalkableFightCell(map, template[i], i);
            } else {
                cells[i] = new WalkableFightCell(map, template[i], i);
            }
        }

        return cells;
    }

    /**
     * @return All objects on the map
     */
    public BattlefieldObjects objects() {
        return objects;
    }
}
