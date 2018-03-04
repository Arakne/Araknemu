package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.di.ItemPoolContainer;
import fr.quatrevieux.araknemu.data.living.repository.implementation.sql.LivingRepositoriesModule;
import fr.quatrevieux.araknemu.data.world.repository.implementation.sql.WorldRepositoriesModule;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.CharactersService;
import fr.quatrevieux.araknemu.game.account.TokenService;
import fr.quatrevieux.araknemu.game.account.generator.NameCheckerGenerator;
import fr.quatrevieux.araknemu.game.account.generator.NameGenerator;
import fr.quatrevieux.araknemu.game.admin.AdminService;
import fr.quatrevieux.araknemu.game.chat.ChatService;
import fr.quatrevieux.araknemu.game.connector.ConnectorService;
import fr.quatrevieux.araknemu.game.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.exploration.ExplorationService;
import fr.quatrevieux.araknemu.game.exploration.action.factory.ExplorationActionFactory;
import fr.quatrevieux.araknemu.game.exploration.area.AreaService;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.effect.mapping.*;
import fr.quatrevieux.araknemu.game.item.factory.DefaultItemFactory;
import fr.quatrevieux.araknemu.game.item.factory.ItemFactory;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryService;
import fr.quatrevieux.araknemu.network.adapter.Server;
import fr.quatrevieux.araknemu.network.adapter.SessionHandler;
import fr.quatrevieux.araknemu.network.adapter.netty.NettyServer;
import fr.quatrevieux.araknemu.network.adapter.util.LoggingSessionHandler;
import fr.quatrevieux.araknemu.network.in.AggregatePacketParser;
import fr.quatrevieux.araknemu.network.in.DefaultDispatcher;
import fr.quatrevieux.araknemu.network.in.Dispatcher;
import fr.quatrevieux.araknemu.network.in.PacketParser;
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
        assertInstanceOf(LoggingSessionHandler.class, container.get(SessionHandler.class));
        assertInstanceOf(NettyServer.class, container.get(Server.class));
        assertInstanceOf(DefaultDispatcher.class, container.get(Dispatcher.class));
        assertInstanceOf(AggregatePacketParser.class, container.get(PacketParser.class));
        assertInstanceOf(ConnectorService.class, container.get(ConnectorService.class));
        assertInstanceOf(TokenService.class, container.get(TokenService.class));
        assertInstanceOf(AccountService.class, container.get(AccountService.class));
        assertInstanceOf(CharactersService.class, container.get(CharactersService.class));
        assertInstanceOf(PlayerService.class, container.get(PlayerService.class));
        assertInstanceOf(ExplorationService.class, container.get(ExplorationService.class));
        assertInstanceOf(ExplorationMapService.class, container.get(ExplorationMapService.class));
        assertInstanceOf(ExplorationActionFactory.class, container.get(ExplorationActionFactory.class));
        assertInstanceOf(DefaultListenerAggregate.class, container.get(ListenerAggregate.class));
        assertInstanceOf(ChatService.class, container.get(ChatService.class));
        assertInstanceOf(NameCheckerGenerator.class, container.get(NameGenerator.class));
        assertInstanceOf(AreaService.class, container.get(AreaService.class));
        assertInstanceOf(AdminService.class, container.get(AdminService.class));
        assertInstanceOf(ItemService.class, container.get(ItemService.class));
        assertInstanceOf(EffectMappers.class, container.get(EffectMappers.class));
        assertInstanceOf(DefaultItemFactory.class, container.get(ItemFactory.class));
        assertInstanceOf(InventoryService.class, container.get(InventoryService.class));

        assertSame(
            container.get(ListenerAggregate.class),
            container.get(fr.quatrevieux.araknemu.game.event.Dispatcher.class)
        );
    }
}