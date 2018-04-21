package fr.quatrevieux.araknemu.game.handler.loader;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.factory.ActionFactory;
import fr.quatrevieux.araknemu.game.handler.EnsureExploring;
import fr.quatrevieux.araknemu.game.handler.game.CancelGameAction;
import fr.quatrevieux.araknemu.game.handler.game.EndGameAction;
import fr.quatrevieux.araknemu.game.handler.game.LoadExtraInfo;
import fr.quatrevieux.araknemu.game.handler.game.ValidateGameAction;
import fr.quatrevieux.araknemu.game.handler.object.UseObject;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Loader for exploration packets
 */
final public class ExploringLoader extends AbstractLoader {
    public ExploringLoader() {
        super(EnsureExploring::new);
    }

    @Override
    public PacketHandler<GameSession, ?>[] handlers(Container container) throws ContainerException {
        return new PacketHandler[] {
            new LoadExtraInfo(),
            new ValidateGameAction(
                container.get(ActionFactory.class)
            ),
            new EndGameAction(),
            new CancelGameAction(),
            new UseObject()
        };
    }
}
