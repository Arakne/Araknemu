package fr.quatrevieux.araknemu.game.exploration.map;

import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.value.Dimensions;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.cell.BasicCell;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.exploration.map.cell.CellLoader;
import fr.quatrevieux.araknemu.game.exploration.map.event.NewSpriteOnMap;
import fr.quatrevieux.araknemu.game.exploration.map.event.SpriteRemoveFromMap;
import fr.quatrevieux.araknemu.game.listener.map.*;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.game.world.map.GameMap;
import fr.quatrevieux.araknemu.game.world.util.Sender;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Map object for exploration
 */
final public class ExplorationMap implements GameMap<ExplorationMapCell>, Dispatcher {
    final private MapTemplate template;

    final private Map<Integer, ExplorationMapCell> cells;
    final private ConcurrentMap<Integer, ExplorationPlayer> players = new ConcurrentHashMap<>();
    final private ListenerAggregate dispatcher = new DefaultListenerAggregate();

    public ExplorationMap(MapTemplate template, CellLoader loader) {
        this.template = template;

        this.cells = loader.load(this, template.cells())
            .stream()
            .collect(Collectors.toMap(ExplorationMapCell::id, Function.identity()))
        ;
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
        return template.cells().size();
    }

    @Override
    public ExplorationMapCell get(int id) {
        if (cells.containsKey(id)) {
            return cells.get(id);
        }

        return new BasicCell(id, template.cells().get(id), this);
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

    /**
     * Can launch a fight on the map ?
     */
    public boolean canLaunchFight() {
        return template.fightPlaces().length >= 2;
    }

    public ListenerAggregate dispatcher() {
        return dispatcher;
    }
}
