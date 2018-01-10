package fr.quatrevieux.araknemu.game.handler.loader;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.TokenService;
import fr.quatrevieux.araknemu.game.handler.CheckQueuePosition;
import fr.quatrevieux.araknemu.game.handler.PongResponse;
import fr.quatrevieux.araknemu.game.handler.StartSession;
import fr.quatrevieux.araknemu.game.handler.StopSession;
import fr.quatrevieux.araknemu.game.handler.account.Login;
import fr.quatrevieux.araknemu.game.handler.account.SendRegionalVersion;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Loader for common packets
 */
final public class CommonLoader implements Loader {
    @Override
    public PacketHandler<GameSession, ?>[] load(Container container) throws ContainerException {
        return new PacketHandler[] {
            new StartSession(),
            new StopSession(),
            new CheckQueuePosition(),
            new Login(
                container.get(TokenService.class),
                container.get(AccountService.class)
            ),
            new SendRegionalVersion(),
            new PongResponse()
        };
    }
}
