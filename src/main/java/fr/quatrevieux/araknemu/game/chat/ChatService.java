package fr.quatrevieux.araknemu.game.chat;

import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.listener.service.RegisterChatListeners;
import org.slf4j.Logger;

/**
 * Service for handle chat system
 */
final public class ChatService implements PreloadableService {
    final private ListenerAggregate dispatcher;

    public ChatService(ListenerAggregate dispatcher) {
        this.dispatcher = dispatcher;
    }

    /**
     * Register listeners for ChatService
     */
    @Override
    public void preload(Logger logger) {
        logger.info("Initialize chat...");

        dispatcher.add(new RegisterChatListeners());
    }
}
