package fr.quatrevieux.araknemu.game.exploration;

import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.exploration.event.CellChanged;
import fr.quatrevieux.araknemu.game.exploration.event.MapChanged;
import fr.quatrevieux.araknemu.game.exploration.event.MapJoined;
import fr.quatrevieux.araknemu.game.exploration.event.MapLeaved;
import fr.quatrevieux.araknemu.game.exploration.interaction.InteractionHandler;
import fr.quatrevieux.araknemu.game.exploration.interaction.event.PlayerMoveFinished;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.player.CharacterProperties;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerSessionScope;
import fr.quatrevieux.araknemu.game.player.inventory.PlayerInventory;
import fr.quatrevieux.araknemu.game.player.sprite.PlayerSprite;
import fr.quatrevieux.araknemu.game.world.creature.Creature;
import fr.quatrevieux.araknemu.game.world.creature.Explorer;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.network.game.GameSession;

/**
 * Player for exploration game session
 */
final public class ExplorationPlayer implements Creature, Explorer, PlayerSessionScope {
    final private GamePlayer player;

    final private ListenerAggregate dispatcher = new DefaultListenerAggregate();
    final private InteractionHandler interactions = new InteractionHandler();

    private ExplorationMap map;

    public ExplorationPlayer(GamePlayer player) {
        this.player = player;
    }

    @Override
    public int id() {
        return player.id();
    }

    public GameAccount account() {
        return player.account();
    }

    @Override
    public CharacterProperties properties() {
        return player.properties();
    }

    @Override
    public void send(Object packet) {
        player.send(packet);
    }

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

        session.setExploration(null);
    }

    @Override
    public Sprite sprite() {
        return new PlayerSprite(player.spriteInfo(), position());
    }

    @Override
    public int cell() {
        return position().cell();
    }

    @Override
    public Position position() {
        return player.position();
    }

    @Override
    public ExplorationMap map() {
        return map;
    }

    @Override
    public void move(ExplorationMapCell cell) {
        player.setPosition(player.position().newCell(cell.id()));
        map.dispatch(new PlayerMoveFinished(this, cell));
    }

    /**
     * Join an exploration map
     */
    public void join(ExplorationMap map) {
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
    public void changeMap(ExplorationMap map, int cell) {
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
    public void changeCell(int cell) {
        player.setPosition(
            player.position().newCell(cell)
        );

        map.dispatch(new CellChanged(this, cell));
    }

    /**
     * Leave the current map
     */
    public void leave() {
        if (map != null) {
            map.remove(this);
            dispatch(new MapLeaved(map));
        }
    }

    /**
     * Get the inventory
     */
    public PlayerInventory inventory() {
        return player.inventory();
    }

    /**
     * Handle player interactions
     */
    public InteractionHandler interactions() {
        return interactions;
    }

    /**
     * Get the player data
     */
    public GamePlayer player() {
         return player;
    }
}
