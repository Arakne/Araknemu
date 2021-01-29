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

import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Map object for exploration
 */
final public class ExplorationMap implements DofusMap<ExplorationMapCell>, Dispatcher {
    final private MapTemplate template;
    final private ExplorationSubArea subArea;

    final private Map<Integer, ExplorationMapCell> cells;
    final private ConcurrentMap<Integer, ExplorationCreature> creatures = new ConcurrentHashMap<>();

    final private ListenerAggregate dispatcher = new DefaultListenerAggregate();

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
    public int id() {
        return template.id();
    }

    /**
     * Get the map data / version
     *
     * filename : data/maps/[id]_[date](X).swf
     */
    public String date() {
        return template.date();
    }

    /**
     * Get the map decryption key
     * Used by map swf which finish with "X.swf"
     */
    public String key() {
        return template.key();
    }

    /**
     * Get the map dimensions
     *
     * /!\ Because cells are interleaved, the real height of the map is x2,
     *     and the width is lower one every two lines
     */
    public Dimensions dimensions() {
        return template.dimensions();
    }

    /**
     * Get the number of cells of the map
     */
    public int size() {
        return template.cells().length;
    }

    @Override
    public ExplorationMapCell get(int id) {
        if (cells.containsKey(id)) {
            return cells.get(id);
        }

        return new BasicCell(id, template.cells()[id], this);
    }

    /**
     * Add a new creature to the map
     */
    public void add(ExplorationCreature creature) {
        if (creatures.containsKey(creature.id())) {
            throw new IllegalArgumentException("The creature is already added");
        }

        creatures.put(creature.id(), creature);

        dispatch(new NewSpriteOnMap(creature.sprite()));
    }

    /**
     * Remove the creature from the map
     */
    public void remove(ExplorationCreature creature) {
        if (!creatures.containsKey(creature.id())) {
            throw new IllegalArgumentException("The creature do not exists");
        }

        creatures.remove(creature.id());
        dispatch(new SpriteRemoveFromMap(creature.sprite()));
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
    public ExplorationCreature creature(int id) {
        if (!creatures.containsKey(id)) {
            throw new NoSuchElementException("The creature " + id + " cannot be found");
        }

        return creatures.get(id);
    }

    /**
     * Check if the map has the creature
     *
     * @param id The creature id
     */
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
     * Apply an operation to all creatures in map
     *
     * @see ExplorationCreature#apply(Operation)
     */
    public void apply(Operation operation) {
        creatures.values().forEach(creature -> creature.apply(operation));
    }

    /**
     * Can launch a fight on the map ?
     */
    public boolean canLaunchFight() {
        return template.fightPlaces().length >= 2;
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
}
