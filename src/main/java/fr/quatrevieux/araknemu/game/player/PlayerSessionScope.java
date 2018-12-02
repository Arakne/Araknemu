package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.world.util.Sender;
import fr.quatrevieux.araknemu.network.game.GameSession;

/**
 * Store player properties for a game session scope
 */
public interface PlayerSessionScope extends Dispatcher, Sender {
    /**
     * Get the session event dispatcher for register listeners and dispatch event only to current scope
     */
    public ListenerAggregate dispatcher();

    /**
     * Get the properties of the current character session
     */
    public CharacterProperties properties();

    /**
     * Register the scope to the session
     */
    public void register(GameSession session);

    /**
     * Remove the scope from the session
     */
    public void unregister(GameSession session);
}
