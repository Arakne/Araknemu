package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.di.ItemPoolContainer;
import fr.quatrevieux.araknemu.data.RepositoriesModule;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.CharactersService;
import fr.quatrevieux.araknemu.game.account.TokenService;
import fr.quatrevieux.araknemu.game.connector.ConnectorService;
import fr.quatrevieux.araknemu.network.LoggedIoHandler;
import fr.quatrevieux.araknemu.network.in.AggregatePacketParser;
import fr.quatrevieux.araknemu.network.in.DefaultDispatcher;
import fr.quatrevieux.araknemu.network.in.Dispatcher;
import fr.quatrevieux.araknemu.network.in.PacketParser;
import org.apache.mina.core.service.IoHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameModuleTest extends GameBaseCase {
    @Test
    void instances() throws ContainerException {
        Container container = new ItemPoolContainer();

        container.register(new ConnectorModule());
        container.register(new RepositoriesModule());
        container.register(new GameModule(app));

        assertInstanceOf(GameService.class, container.get(GameService.class));
        assertInstanceOf(LoggedIoHandler.class, container.get(IoHandler.class));
        assertInstanceOf(DefaultDispatcher.class, container.get(Dispatcher.class));
        assertInstanceOf(AggregatePacketParser.class, container.get(PacketParser.class));
        assertInstanceOf(ConnectorService.class, container.get(ConnectorService.class));
        assertInstanceOf(TokenService.class, container.get(TokenService.class));
        assertInstanceOf(AccountService.class, container.get(AccountService.class));
        assertInstanceOf(CharactersService.class, container.get(CharactersService.class));
    }

    public void assertInstanceOf(Class type, Object obj) {
        assertTrue(type.isInstance(obj));
    }
}