package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.exploration.MapLoaded;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.network.game.GameSession;

/**
 * GamePlayer object
 * A player is a logged character, with associated game session
 */
final public class GamePlayer extends AbstractCharacter implements Dispatcher {
    final private GameSession session;
    final private PlayerRace race;
    final private PlayerCharacteristics characteristics;

    final private ListenerAggregate dispatcher = new DefaultListenerAggregate();

    /**
     * The current map
     */
    private ExplorationMap map;

    public GamePlayer(GameAccount account, Player entity, PlayerRace race, GameSession session) {
        super(account, entity);

        this.race = race;
        this.session = session;

        characteristics = new PlayerCharacteristics(
            new BaseCharacteristics(
                race.baseStats(),
                entity.stats()
            )
        );
    }

    @Override
    public void dispatch(Object event) {
        dispatcher.dispatch(event);
    }

    public ListenerAggregate dispatcher() {
        return dispatcher;
    }

    public PlayerCharacteristics characteristics() {
        return characteristics;
    }

    /**
     * Send a packet to the player
     */
    public void send(Object packet) {
        session.write(packet);
    }

    public Sprite sprite() {
        return new PlayerSprite(this);
    }

    /**
     * Get the current player position
     *
     * @see GamePlayer#map() For get the current map
     */
    public Position position() {
        return entity.position();
    }

    /**
     * Get the current exploration map
     *
     * @see GamePlayer#position() For get the player position
     */
    public ExplorationMap map() {
        return map;
    }

    /**
     * Change the current player position
     */
    public void goTo(Position position) {
        entity.setPosition(position);
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
     * Get the player race
     */
    public PlayerRace race() {
        return race;
    }
}
