package fr.quatrevieux.araknemu.game.exploration.map;

import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.exploration.NewSpriteOnMap;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Map object for exploration
 */
final public class ExplorationMap {
    public class Cell {
        final private MapTemplate.Cell template;

        public Cell(MapTemplate.Cell template) {
            this.template = template;
        }
    }

    final private MapTemplate template;
    final private List<Cell> cells;
    final private ConcurrentMap<Integer, GamePlayer> players = new ConcurrentHashMap<>();

    public ExplorationMap(MapTemplate template) {
        this.template = template;

        cells = template.cells()
            .stream()
            .map(Cell::new)
            .collect(Collectors.toList())
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

    public Cell cell(int id) {
        return cells.get(id);
    }

    /**
     * Add a new player to the map
     */
    public void add(GamePlayer player) {
        if (players.containsKey(player.id())) {
            throw new IllegalArgumentException("The player is already added");
        }

        players.put(player.id(), player);

        dispatch(new NewSpriteOnMap(player.sprite()));
    }

    /**
     * Get list of map sprites
     */
    public Collection<Sprite> sprites() {
        return players
            .values()
            .stream()
            .map(GamePlayer::sprite)
            .collect(Collectors.toList())
        ;
    }

    private void dispatch(Object event) {
        for (Dispatcher player : players.values()) {
            player.dispatch(event);
        }
    }
}
