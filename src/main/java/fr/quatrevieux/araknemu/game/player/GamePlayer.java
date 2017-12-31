package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
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
}
