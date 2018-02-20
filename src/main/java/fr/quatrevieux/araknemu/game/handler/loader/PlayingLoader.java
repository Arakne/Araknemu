package fr.quatrevieux.araknemu.game.handler.loader;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.chat.ChatService;
import fr.quatrevieux.araknemu.game.exploration.ExplorationService;
import fr.quatrevieux.araknemu.game.handler.EnsurePlaying;
import fr.quatrevieux.araknemu.game.handler.account.BoostCharacteristic;
import fr.quatrevieux.araknemu.game.handler.chat.SaveSubscription;
import fr.quatrevieux.araknemu.game.handler.chat.SendMessage;
import fr.quatrevieux.araknemu.game.handler.game.CreateGame;
import fr.quatrevieux.araknemu.game.handler.object.MoveObject;
import fr.quatrevieux.araknemu.game.handler.object.RemoveObject;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Loader for playing packets
 */
final public class PlayingLoader extends AbstractLoader {
    public PlayingLoader() {
        super(EnsurePlaying::new);
    }

    @Override
    protected PacketHandler<GameSession, ?>[] handlers(Container container) throws ContainerException {
        return new PacketHandler[] {
            new CreateGame(
                container.get(ExplorationService.class)
            ),
            new SendMessage(
                container.get(ChatService.class)
            ),
            new SaveSubscription(),
            new MoveObject(),
            new BoostCharacteristic(),
            new RemoveObject()
        };
    }
}
