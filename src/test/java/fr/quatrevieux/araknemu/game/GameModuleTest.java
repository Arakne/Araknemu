package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.di.ItemPoolContainer;
import fr.quatrevieux.araknemu.data.living.repository.implementation.sql.LivingRepositoriesModule;
import fr.quatrevieux.araknemu.data.world.repository.implementation.sql.WorldRepositoriesModule;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.CharactersService;
import fr.quatrevieux.araknemu.game.account.TokenService;
import fr.quatrevieux.araknemu.game.connector.ConnectorService;
import fr.quatrevieux.araknemu.game.exploration.ExplorationService;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.network.LoggedIoHandler;
import fr.quatrevieux.araknemu.network.in.AggregatePacketParser;
import fr.quatrevieux.araknemu.network.in.DefaultDispatcher;
import fr.quatrevieux.araknemu.network.in.Dispatcher;
import fr.quatrevieux.araknemu.network.in.PacketParser;
import org.apache.mina.core.service.IoHandler;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class GameModuleTest extends GameBaseCase {
    @Test
    void instances() throws ContainerException, SQLException {
        Container container = new ItemPoolContainer();

        container.register(new ConnectorModule());
        container.register(new LivingRepositoriesModule(app.database().get("game")));
        container.register(new WorldRepositoriesModule(app.database().get("game")));
        container.register(new GameModule(app));

        assertInstanceOf(GameService.class, container.get(GameService.class));
        assertInstanceOf(LoggedIoHandler.class, container.get(IoHandler.class));
        assertInstanceOf(DefaultDispatcher.class, container.get(Dispatcher.class));
        assertInstanceOf(AggregatePacketParser.class, container.get(PacketParser.class));
        assertInstanceOf(ConnectorService.class, container.get(ConnectorService.class));
        assertInstanceOf(TokenService.class, container.get(TokenService.class));
        assertInstanceOf(AccountService.class, container.get(AccountService.class));
        assertInstanceOf(CharactersService.class, container.get(CharactersService.class));
        assertInstanceOf(PlayerService.class, container.get(PlayerService.class));
        assertInstanceOf(ExplorationService.class, container.get(ExplorationService.class));
        assertInstanceOf(ExplorationMapService.class, container.get(ExplorationMapService.class));
    }

    public void assertInstanceOf(Class type, Object obj) {
        assertTrue(type.isInstance(obj));
    }
}