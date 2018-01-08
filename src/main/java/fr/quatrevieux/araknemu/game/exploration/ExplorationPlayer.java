package fr.quatrevieux.araknemu.game.exploration;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.exploration.MapLoaded;
import fr.quatrevieux.araknemu.game.exploration.action.ActionQueue;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.player.*;
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
    final private ActionQueue actionQueue = new ActionQueue();

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
        dispatcher.dispatch(event);
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
     * Get the dispatcher
     */
    ListenerAggregate dispatcher() {
        return dispatcher;
    }

    /**
     * @todo Do not use getter
     */
    public ActionQueue actionQueue() {
        return actionQueue;
    }
}
