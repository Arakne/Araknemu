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

package fr.quatrevieux.araknemu.game.exploration.map;

import fr.arakne.utils.maps.DofusMap;
import fr.arakne.utils.value.Dimensions;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.value.Geolocation;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.game.exploration.area.ExplorationSubArea;
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;
import fr.quatrevieux.araknemu.game.exploration.creature.Operation;
import fr.quatrevieux.araknemu.game.exploration.creature.operation.SendPacket;
import fr.quatrevieux.araknemu.game.exploration.map.cell.BasicCell;
import fr.quatrevieux.araknemu.game.exploration.map.cell.CellLoader;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.exploration.map.event.NewSpriteOnMap;
import fr.quatrevieux.araknemu.game.exploration.map.event.SpriteRemoveFromMap;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import org.checkerframework.checker.index.qual.IndexFor;
import org.checkerframework.checker.index.qual.LengthOf;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.dataflow.qual.SideEffectFree;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Map object for exploration
 */
public final class ExplorationMap implements DofusMap<ExplorationMapCell>, Dispatcher {
    private final MapTemplate template;
    private final ExplorationSubArea subArea;

    private final Map<@NonNegative Integer, ExplorationMapCell> cells;
    private final ConcurrentMap<Integer, ExplorationCreature> creatures = new ConcurrentHashMap<>();

    private final ListenerAggregate dispatcher = new DefaultListenerAggregate();

    @SuppressWarnings({"argument"})
    public ExplorationMap(MapTemplate template, CellLoader loader, ExplorationSubArea subArea) {
        this.template = template;
        this.subArea = subArea;

        this.cells = loader.load(this, template.cells())
            .stream()
            .collect(Collectors.toMap(ExplorationMapCell::id, Function.identity()))
        ;
    }

    /**
     * Get the map id
     *
     * filename : data/maps/[id]_[date](X).swf
     */
    @Pure
    public @NonNegative int id() {
        return template.id();
    }

    /**
     * Get the map data / version
     *
     * filename : data/maps/[id]_[date](X).swf
     */
    @Pure
    public String date() {
        return template.date();
    }

    /**
     * Get the map decryption key
     * Used by map swf which finish with "X.swf"
     */
    @Pure
    public String key() {
        return template.key();
    }

    /**
     * Get the map dimensions
     *
     * /!\ Because cells are interleaved, the real height of the map is x2,
     *     and the width is lower one every two lines
     */
    @Pure
    @Override
    public Dimensions dimensions() {
        return template.dimensions();
    }

    /**
     * Get the number of cells of the map
     */
    @Pure
    @Override
    @SuppressWarnings("return") // Cannot infer template.cells().length to be equals to this length
    public @LengthOf("this") int size() {
        return template.cells().length;
    }

    @Override
    @SideEffectFree
    @SuppressWarnings("array.access.unsafe.high") // Cannot infer template.cells().length to be equals to this length
    public ExplorationMapCell get(@IndexFor("this") int id) {
        final ExplorationMapCell cell = cells.get(id);

        if (cell != null) {
            return cell;
        }

        return new BasicCell(id, template.cells()[id], this);
    }

    /**
     * Add a new creature to the map
     * Will trigger a {@link NewSpriteOnMap} after the creature is successfully added
     *
     * @throws IllegalArgumentException If a creature with the same id is already present
     */
    public void add(ExplorationCreature creature) {
        final int id = creature.id();
        final ExplorationCreature previous = creatures.putIfAbsent(id, creature);

        if (previous != null) {
            throw new IllegalArgumentException("The creature is already added");
        }

        dispatch(new NewSpriteOnMap(creature.sprite()));
    }

    /**
     * Remove the creature from the map
     * Will trigger a {@link SpriteRemoveFromMap} event if the creature was present
     *
     * @return true if the creature was removed, false if the creature do not exist
     */
    public boolean remove(ExplorationCreature creature) {
        final ExplorationCreature removed = creatures.remove(creature.id());

        if (removed == null || !removed.equals(creature)) {
            return false;
        }

        dispatch(new SpriteRemoveFromMap(creature.sprite()));

        return true;
    }

    /**
     * Get list of map sprites
     */
    public Collection<Sprite> sprites() {
        return creatures.values().stream()
            .map(ExplorationCreature::sprite)
            .collect(Collectors.toList())
        ;
    }

    /**
     * Get all creatures on map
     */
    public Collection<ExplorationCreature> creatures() {
        return creatures.values();
    }

    /**
     * Get a creature by its id
     *
     * @param id The creature id
     */
    @Pure
    public ExplorationCreature creature(int id) {
        final ExplorationCreature creature = creatures.get(id);

        if (creature != null) {
            return creature;
        }

        throw new NoSuchElementException("The creature " + id + " cannot be found");
    }

    /**
     * Check if the map has the creature
     *
     * @param id The creature id
     */
    @Pure
    public boolean has(int id) {
        return creatures.containsKey(id);
    }

    @Override
    public void dispatch(Object event) {
        dispatcher.dispatch(event);
    }

    /**
     * Send a packet to the map
     */
    public void send(Object packet) {
        apply(new SendPacket(packet));
    }

    /**
     * Apply an operation to all creatures in map, and return the first non-null operation value
     * If the operation return a value, the iteration will be stopped
     *
     * @return The first non-null operation value
     *
     * @see ExplorationCreature#apply(Operation)
     */
    public <R> @Nullable R apply(Operation<R> operation) {
        for (ExplorationCreature creature : creatures.values()) {
            final R result = creature.apply(operation);

            if (result != null) {
                return result;
            }
        }

        return null;
    }

    /**
     * Can launch a fight on the map ?
     */
    @Pure
    public boolean canLaunchFight() {
        return template.fightPlaces().length >= 2;
    }

    /**
     * Get the available fight places for the given team
     *
     * @param team The team number. Starts at 0
     *
     * @return List of placement cells, or empty list if there is not place for the given team
     */
    @SuppressWarnings("methodref.param") // places are considered as safe cell ids
    public List<ExplorationMapCell> fightPlaces(@NonNegative int team) {
        final int[][] places = template.fightPlaces();

        if (team >= places.length) {
            return Collections.emptyList();
        }

        return Arrays.stream(places[team]).mapToObj(this::get).collect(Collectors.toList());
    }

    /**
     * Get the sub-area of the map
     */
    public ExplorationSubArea subArea() {
        return subArea;
    }

    /**
     * Check if the map is an indoor map (i.e. house or underground)
     */
    public boolean indoor() {
        return template.indoor();
    }

    /**
     * Get the map geolocation
     */
    public Geolocation geolocation() {
        return template.geolocation();
    }

    public ListenerAggregate dispatcher() {
        return dispatcher;
    }

    /**
     * Internal: do not call directly !
     */
    public MapTemplate template() {
        return template;
    }
}
