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

package fr.quatrevieux.araknemu.game.exploration;

import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;
import fr.quatrevieux.araknemu.game.exploration.creature.Explorer;
import fr.quatrevieux.araknemu.game.exploration.creature.Operation;
import fr.quatrevieux.araknemu.game.exploration.event.CellChanged;
import fr.quatrevieux.araknemu.game.exploration.event.MapChanged;
import fr.quatrevieux.araknemu.game.exploration.event.MapJoined;
import fr.quatrevieux.araknemu.game.exploration.event.MapLeaved;
import fr.quatrevieux.araknemu.game.exploration.event.OrientationChanged;
import fr.quatrevieux.araknemu.game.exploration.event.StopExploration;
import fr.quatrevieux.araknemu.game.exploration.interaction.InteractionHandler;
import fr.quatrevieux.araknemu.game.exploration.interaction.event.PlayerMoveFinished;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.exploration.sprite.PlayerSprite;
import fr.quatrevieux.araknemu.game.player.CharacterProperties;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerSessionScope;
import fr.quatrevieux.araknemu.game.player.inventory.PlayerInventory;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.network.game.GameSession;
import org.checkerframework.checker.index.qual.IndexFor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;

/**
 * Player for exploration game session
 */
public final class ExplorationPlayer implements ExplorationCreature, Explorer, PlayerSessionScope {
    private final GamePlayer player;

    private final ListenerAggregate dispatcher = new DefaultListenerAggregate();
    private final InteractionHandler interactions = new InteractionHandler();
    private final Restrictions restrictions;
    private final Sprite sprite;

    private @Nullable ExplorationMap map;
    private @Nullable ExplorationMapCell cell;
    private Direction orientation = Direction.SOUTH_EAST;

    @SuppressWarnings({"assignment", "argument"})
    public ExplorationPlayer(GamePlayer player) {
        this.player = player;
        this.restrictions = new Restrictions(this);
        this.sprite = new PlayerSprite(this);

        restrictions.refresh();
    }

    @Pure
    @Override
    public int id() {
        return player.id();
    }

    @Pure
    public GameAccount account() {
        return player.account();
    }

    @Pure
    @Override
    public CharacterProperties properties() {
        return player.properties();
    }

    @Override
    public void send(Object packet) {
        player.send(packet);
    }

    @Pure
    @Override
    public ListenerAggregate dispatcher() {
        return dispatcher;
    }

    @Override
    public void dispatch(Object event) {
        player.dispatch(event);
    }

    @Override
    public void register(GameSession session) {
        session.setExploration(this);
    }

    @Override
    public void unregister(GameSession session) {
        leave();
        interactions().stop();

        dispatch(new StopExploration(session, this));
        session.setExploration(null);
    }

    @Pure
    @Override
    public Sprite sprite() {
        return sprite;
    }

    @Pure
    @Override
    public ExplorationMapCell cell() {
        if (cell == null) {
            throw new IllegalStateException("The player has not join a map");
        }

        return cell;
    }

    @Pure
    @Override
    public Position position() {
        return player.position();
    }

    @Pure
    @Override
    public Direction orientation() {
        return orientation;
    }

    /**
     * @todo Returns {@code Optional<ExplorationMap>}
     */
    @Pure
    @Override
    public @Nullable ExplorationMap map() {
        return map;
    }

    @Override
    public void move(ExplorationMapCell cell, Direction orientation) {
        final ExplorationMap map = this.map;

        if (map == null || !cell.map().equals(map)) {
            throw new IllegalArgumentException("Cell is not on the current map");
        }

        player.setPosition(player.position().newCell(cell.id()));
        this.cell = cell;
        this.orientation = orientation;

        map.dispatch(new PlayerMoveFinished(this, cell));
    }

    @Override
    public <R> @Nullable R apply(Operation<R> operation) {
        return operation.onExplorationPlayer(this);
    }

    /**
     * Join an exploration map
     *
     * @throws IllegalArgumentException When the joined map do not corresponds with player's position
     */
    public void join(ExplorationMap map) {
        if (position().map() != map.id()) {
            throw new IllegalArgumentException("Map id do not corresponds with player's position");
        }

        if (position().cell() >= map.size()) {
            throw new IllegalStateException("Invalid cell");
        }

        this.cell = map.get(position().cell());
        this.map = map;
        map.add(this);

        dispatch(new MapJoined(map));
    }

    /**
     * Change the current map and cell
     *
     * @param map The new map
     * @param cell The new cell
     */
    public void changeMap(ExplorationMap map, @IndexFor("#1") int cell) {
        this.map = null;
        this.cell = null;
        player.setPosition(
            new Position(map.id(), cell)
        );

        join(map);
        dispatch(new MapChanged(map));
    }

    /**
     * Change the current cell of the player
     *
     * @param cell The new cell id
     *
     * @see ExplorationPlayer#changeMap(ExplorationMap, int) For changing the map and cell
     */
    public void changeCell(@IndexFor("this.map()") int cell) {
        final ExplorationMap map = this.map;

        if (map == null) {
            throw new IllegalStateException("Player is not on map");
        }

        if (cell >= map.size()) {
            throw new IllegalArgumentException("The cell " + cell + " do exists on map " + map.id());
        }

        player.setPosition(player.position().newCell(cell));
        this.cell = map.get(cell);

        map.dispatch(new CellChanged(this, cell));
    }

    /**
     * Leave the current map
     */
    public void leave() {
        if (map != null) {
            map.remove(this);
            dispatch(new MapLeaved(map));
            map = null;
            cell = null;
        }
    }

    /**
     * Get the inventory
     */
    @Pure
    public PlayerInventory inventory() {
        return player.inventory();
    }

    /**
     * Handle player interactions
     */
    @Pure
    public InteractionHandler interactions() {
        return interactions;
    }

    /**
     * Get the player data
     */
    @Pure
    public GamePlayer player() {
         return player;
    }

    /**
     * Get the restrictions of the exploration player
     */
    @Pure
    public Restrictions restrictions() {
        return restrictions;
    }

    /**
     * Change the player's orientation
     */
    public void setOrientation(Direction orientation) {
        this.orientation = orientation;

        if (map != null) {
            map.dispatch(new OrientationChanged(this, orientation));
        }
    }
}
