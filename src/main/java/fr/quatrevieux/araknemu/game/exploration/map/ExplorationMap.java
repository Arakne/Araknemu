package fr.quatrevieux.araknemu.game.exploration.map;

import fr.quatrevieux.araknemu.data.value.Dimensions;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.game.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.exploration.NewSpriteOnMap;
import fr.quatrevieux.araknemu.game.event.exploration.SpriteRemoveFromMap;
import fr.quatrevieux.araknemu.game.event.listener.map.*;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.trigger.MapTriggers;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.game.world.map.Decoder;
import fr.quatrevieux.araknemu.game.world.util.Sender;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Map object for exploration
 *
 * @todo optimize for inactive cells
 */
final public class ExplorationMap implements Dispatcher {
    public class Cell {
        final private MapTemplate.Cell template;

        public Cell(MapTemplate.Cell template) {
            this.template = template;
        }

        /**
         * Can walk on this cell ?
         */
        public boolean isWalkable() {
            return template.active() && template.movement() > 0;
        }
    }

    final private MapTemplate template;
    final private MapTriggers triggers;

    final private List<Cell> cells;
    final private ConcurrentMap<Integer, ExplorationPlayer> players = new ConcurrentHashMap<>();
    final private ListenerAggregate dispatcher = new DefaultListenerAggregate();

    public ExplorationMap(MapTemplate template, MapTriggers triggers) {
        this.template = template;
        this.triggers = triggers;

        cells = template.cells()
            .stream()
            .map(Cell::new)
            .collect(Collectors.toList())
        ;

        dispatcher.add(new SendNewSprite(this));
        dispatcher.add(new ValidatePlayerPath(this));
        dispatcher.add(new SendPlayerMove(this));
        dispatcher.add(new SendSpriteRemoved(this));
        dispatcher.add(new PerformCellActions(triggers));
        dispatcher.add(new SendPlayerChangeCell(this));
    }

    public int id() {
        return template.id();
    }

    public String date() {
        return template.date();
    }

    public String key() {
        return template.key();
    }

    public Dimensions dimensions() {
        return template.dimensions();
    }

    /**
     * Get the number of cells of the map
     */
    public int size() {
        return cells.size();
    }

    public Cell cell(int id) {
        return cells.get(id);
    }

    /**
     * Add a new player to the map
     */
    public void add(ExplorationPlayer player) {
        if (players.containsKey(player.id())) {
            throw new IllegalArgumentException("The player is already added");
        }

        players.put(player.id(), player);

        dispatch(new NewSpriteOnMap(player.sprite()));
    }

    /**
     * Remove the player from the map
     */
    public void remove(ExplorationPlayer player) {
        if (!players.containsKey(player.id())) {
            throw new IllegalArgumentException("The player do not exists");
        }

        players.remove(player.id());
        dispatch(new SpriteRemoveFromMap(player.sprite()));
    }

    /**
     * Get list of map sprites
     */
    public Collection<Sprite> sprites() {
        return players
            .values()
            .stream()
            .map(ExplorationPlayer::sprite)
            .collect(Collectors.toList())
        ;
    }

    /**
     * Get all players on map
     */
    public Collection<ExplorationPlayer> players() {
        return players.values();
    }

    /**
     * Get the player by its id
     *
     * @param id The player id
     *
     * @return The player or null
     */
    public ExplorationPlayer getPlayer(int id) {
        return players.get(id);
    }

    /**
     * Get the map decoder
     */
    public Decoder decoder() {
        return new Decoder(this);
    }

    @Override
    public void dispatch(Object event) {
        dispatcher.dispatch(event);
    }

    /**
     * Send a packet to the map
     */
    public void send(Object packet) {
        String str = packet.toString(); // Store string value for optimisation

        for (Sender player : players.values()) {
            player.send(str);
        }
    }

    ListenerAggregate dispatcher() {
        return dispatcher;
    }
}
