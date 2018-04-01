package fr.quatrevieux.araknemu.game.exploration;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.exploration.CellChanged;
import fr.quatrevieux.araknemu.game.event.exploration.MapChanged;
import fr.quatrevieux.araknemu.game.event.exploration.MapLeaved;
import fr.quatrevieux.araknemu.game.event.exploration.MapLoaded;
import fr.quatrevieux.araknemu.game.exploration.interaction.InteractionHandler;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.player.*;
import fr.quatrevieux.araknemu.game.player.characteristic.Life;
import fr.quatrevieux.araknemu.game.player.characteristic.PlayerCharacteristics;
import fr.quatrevieux.araknemu.game.player.experience.PlayerLevel;
import fr.quatrevieux.araknemu.game.player.inventory.PlayerInventory;
import fr.quatrevieux.araknemu.game.player.spell.SpellBook;
import fr.quatrevieux.araknemu.game.world.creature.Creature;
import fr.quatrevieux.araknemu.game.world.creature.Explorer;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.game.world.util.Sender;

/**
 * Player for exploration game session
 */
final public class ExplorationPlayer implements PlayableCharacter, Sender, Creature, Dispatcher, Explorer, PlayerData {
    final private GamePlayer player;

    final private ListenerAggregate dispatcher = new DefaultListenerAggregate();
    final private InteractionHandler interactions = new InteractionHandler();

    private ExplorationMap map;

    public ExplorationPlayer(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void print(Printer printer) {
        player.print(printer);
    }

    @Override
    public int id() {
        return player.id();
    }

    @Override
    public GameAccount account() {
        return player.account();
    }

    @Override
    public PlayerCharacteristics characteristics() {
        return player.characteristics();
    }

    @Override
    public Life life() {
        return player.life();
    }

    @Override
    public void send(Object packet) {
        player.send(packet);
    }

    @Override
    public Sprite sprite() {
        return new PlayerSprite(player);
    }

    @Override
    public int cell() {
        return position().cell();
    }

    @Override
    public void dispatch(Object event) {
        player.dispatch(event);
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
    public void move(int cell) {
        player.setPosition(
            player.position().newCell(cell)
        );
    }

    /**
     * Join an exploration map
     */
    public void join(ExplorationMap map) {
        this.map = map;
        map.add(this);

        dispatch(new MapLoaded(map));
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
        map.remove(this);

        dispatch(new MapLeaved(map));
    }

    /**
     * Get the inventory
     */
    public PlayerInventory inventory() {
        return player.inventory();
    }

    /**
     * Get the dispatcher
     */
    public ListenerAggregate dispatcher() {
        return dispatcher;
    }

    /**
     * Handle player interactions
     */
    public InteractionHandler interactions() {
        return interactions;
    }

    /**
     * @todo to remove
     */
    public SpellBook spells() {
        return player.spells();
    }

    @Override
    public PlayerLevel level() {
        return player.level();
    }

    /**
     * Get the player data
     */
    public GamePlayer player() {
         return player;
    }

    /**
     * Stop the exploration
     */
    public void stopExploring() {
        player.stopExploring();
    }
}
