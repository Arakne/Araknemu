package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.di.ItemPoolContainer;
import fr.quatrevieux.araknemu.data.living.repository.implementation.sql.SqlLivingRepositoriesModule;
import fr.quatrevieux.araknemu.data.world.repository.implementation.sql.SqlWorldRepositoriesModule;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.CharactersService;
import fr.quatrevieux.araknemu.game.account.TokenService;
import fr.quatrevieux.araknemu.game.account.generator.NameCheckerGenerator;
import fr.quatrevieux.araknemu.game.account.generator.NameGenerator;
import fr.quatrevieux.araknemu.game.admin.AdminService;
import fr.quatrevieux.araknemu.game.chat.ChatService;
import fr.quatrevieux.araknemu.game.connector.ConnectorService;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.exploration.ExplorationService;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionFactory;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ExplorationActionRegistry;
import fr.quatrevieux.araknemu.game.exploration.area.AreaService;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.CellLoader;
import fr.quatrevieux.araknemu.game.exploration.map.cell.CellLoaderAggregate;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.MapTriggerService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.ActionFactoryRegistry;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.CellActionFactory;
import fr.quatrevieux.araknemu.game.exploration.npc.NpcService;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.DialogService;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.parameter.ParametersResolver;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.DefaultFighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.type.PvmType;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.effect.mapping.*;
import fr.quatrevieux.araknemu.game.item.factory.DefaultItemFactory;
import fr.quatrevieux.araknemu.game.item.factory.ItemFactory;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.monster.environment.MonsterEnvironmentService;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroupFactory;
import fr.quatrevieux.araknemu.game.monster.reward.MonsterRewardService;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.game.player.experience.PlayerExperienceService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryService;
import fr.quatrevieux.araknemu.game.player.race.PlayerRaceService;
import fr.quatrevieux.araknemu.game.player.spell.SpellBookService;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffectService;
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
        container.register(new SqlLivingRepositoriesModule(app.database().get("game")));
        container.register(new SqlWorldRepositoriesModule(app.database().get("game")));
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
        assertInstanceOf(ExplorationActionRegistry.class, container.get(ActionFactory.class));
        assertInstanceOf(DefaultListenerAggregate.class, container.get(ListenerAggregate.class));
        assertInstanceOf(ChatService.class, container.get(ChatService.class));
        assertInstanceOf(NameCheckerGenerator.class, container.get(NameGenerator.class));
        assertInstanceOf(AreaService.class, container.get(AreaService.class));
        assertInstanceOf(AdminService.class, container.get(AdminService.class));
        assertInstanceOf(ItemService.class, container.get(ItemService.class));
        assertInstanceOf(EffectMappers.class, container.get(EffectMappers.class));
        assertInstanceOf(DefaultItemFactory.class, container.get(ItemFactory.class));
        assertInstanceOf(InventoryService.class, container.get(InventoryService.class));
        assertInstanceOf(SpellService.class, container.get(SpellService.class));
        assertInstanceOf(SpellBookService.class, container.get(SpellBookService.class));
        assertInstanceOf(PlayerRaceService.class, container.get(PlayerRaceService.class));
        assertInstanceOf(PlayerExperienceService.class, container.get(PlayerExperienceService.class));
        assertInstanceOf(FightService.class, container.get(FightService.class));
        assertInstanceOf(CellLoaderAggregate.class, container.get(CellLoader.class));
        assertInstanceOf(MapTriggerService.class, container.get(MapTriggerService.class));
        assertInstanceOf(ActionFactoryRegistry.class, container.get(CellActionFactory.class));
        assertInstanceOf(SpellEffectService.class, container.get(SpellEffectService.class));
        assertInstanceOf(DefaultFighterFactory.class, container.get(FighterFactory.class));
        assertInstanceOf(NpcService.class, container.get(NpcService.class));
        assertInstanceOf(DialogService.class, container.get(DialogService.class));
        assertInstanceOf(ParametersResolver.class, container.get(ParametersResolver.class));
        assertInstanceOf(MonsterService.class, container.get(MonsterService.class));
        assertInstanceOf(MonsterGroupFactory.class, container.get(MonsterGroupFactory.class));
        assertInstanceOf(MonsterEnvironmentService.class, container.get(MonsterEnvironmentService.class));
        assertInstanceOf(MonsterRewardService.class, container.get(MonsterRewardService.class));
        assertInstanceOf(PvmType.class, container.get(PvmType.class));

        assertSame(
            container.get(ListenerAggregate.class),
            container.get(fr.quatrevieux.araknemu.core.event.Dispatcher.class)
        );
    }
}