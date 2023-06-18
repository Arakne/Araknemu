/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.common.account.banishment.BanIpService;
import fr.quatrevieux.araknemu.common.account.banishment.BanishmentService;
import fr.quatrevieux.araknemu.common.session.SessionLogService;
import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.di.ItemPoolContainer;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.core.network.Server;
import fr.quatrevieux.araknemu.core.network.netty.NettyServer;
import fr.quatrevieux.araknemu.core.network.parser.AggregatePacketParser;
import fr.quatrevieux.araknemu.core.network.parser.DefaultDispatcher;
import fr.quatrevieux.araknemu.core.network.parser.Dispatcher;
import fr.quatrevieux.araknemu.core.network.parser.PacketParser;
import fr.quatrevieux.araknemu.core.network.session.SessionConfigurator;
import fr.quatrevieux.araknemu.core.network.session.SessionFactory;
import fr.quatrevieux.araknemu.data.living.repository.implementation.sql.SqlLivingRepositoriesModule;
import fr.quatrevieux.araknemu.data.world.repository.implementation.sql.SqlWorldRepositoriesModule;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.CharactersService;
import fr.quatrevieux.araknemu.game.account.TokenService;
import fr.quatrevieux.araknemu.game.account.bank.BankService;
import fr.quatrevieux.araknemu.game.account.generator.NameCheckerGenerator;
import fr.quatrevieux.araknemu.game.account.generator.NameGenerator;
import fr.quatrevieux.araknemu.game.activity.ActivityService;
import fr.quatrevieux.araknemu.game.admin.AdminModule;
import fr.quatrevieux.araknemu.game.chat.ChatService;
import fr.quatrevieux.araknemu.game.connector.ConnectorService;
import fr.quatrevieux.araknemu.game.exploration.ExplorationService;
import fr.quatrevieux.araknemu.game.exploration.area.AreaService;
import fr.quatrevieux.araknemu.game.exploration.exchange.DefaultExchangeFactory;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeFactory;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionFactory;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ExplorationActionRegistry;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.GeolocationService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.CellLoader;
import fr.quatrevieux.araknemu.game.exploration.map.cell.CellLoaderAggregate;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.MapTriggerService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.ActionFactoryRegistry;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.CellActionFactory;
import fr.quatrevieux.araknemu.game.exploration.npc.NpcService;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.DialogService;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.parameter.ParametersResolver;
import fr.quatrevieux.araknemu.game.exploration.npc.exchange.NpcExchangeService;
import fr.quatrevieux.araknemu.game.exploration.npc.store.NpcStoreService;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.ai.factory.AiFactory;
import fr.quatrevieux.araknemu.game.fight.ai.factory.ChainAiFactory;
import fr.quatrevieux.araknemu.game.fight.ai.factory.DoubleAiFactory;
import fr.quatrevieux.araknemu.game.fight.ai.factory.MonsterAiFactory;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.fighter.DefaultFighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.spectator.DefaultSpectatorFactory;
import fr.quatrevieux.araknemu.game.fight.spectator.SpectatorFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.CastFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.closeCombat.CloseCombatFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.FightActionsFactoryRegistry;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.BaseCriticalityStrategy;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.CriticalityStrategy;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.game.fight.type.PvmType;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToCharacteristicMapping;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToSpecialMapping;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToUseMapping;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToWeaponMapping;
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
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertSame;

class GameModuleTest extends GameBaseCase {
    @Test
    void instances() throws ContainerException, SQLException {
        Container container = new ItemPoolContainer();

        container.register(new ConnectorModule());
        container.register(new SqlLivingRepositoriesModule(app.database().get("game")));
        container.register(new SqlWorldRepositoriesModule(app.database().get("game")));
        container.register(new GameModule(app));
        container.register(new AdminModule(app)); // @todo should be removed

        assertInstanceOf(GameService.class, container.get(GameService.class));
        assertInstanceOf(SessionConfigurator.class, container.get(SessionFactory.class));
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
        assertInstanceOf(ItemService.class, container.get(ItemService.class));
        assertInstanceOf(EffectToSpecialMapping.class, container.get(EffectToSpecialMapping.class));
        assertInstanceOf(EffectToWeaponMapping.class, container.get(EffectToWeaponMapping.class));
        assertInstanceOf(EffectToCharacteristicMapping.class, container.get(EffectToCharacteristicMapping.class));
        assertInstanceOf(EffectToUseMapping.class, container.get(EffectToUseMapping.class));
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
        assertInstanceOf(ChallengeType.class, container.get(ChallengeType.class));
        assertInstanceOf(ChainAiFactory.class, container.get(AiFactory.class));
        assertInstanceOf(MonsterAiFactory.class, container.get(MonsterAiFactory.class));
        assertInstanceOf(DoubleAiFactory.class, container.get(DoubleAiFactory.class));
        assertInstanceOf(ActivityService.class, container.get(ActivityService.class));
        assertInstanceOf(DefaultExchangeFactory.class, container.get(ExchangeFactory.class));
        assertInstanceOf(BankService.class, container.get(BankService.class));
        assertInstanceOf(NpcStoreService.class, container.get(NpcStoreService.class));
        assertInstanceOf(NpcExchangeService.class, container.get(NpcExchangeService.class));
        assertInstanceOf(GeolocationService.class, container.get(GeolocationService.class));
        assertInstanceOf(ShutdownService.class, container.get(ShutdownService.class));
        assertInstanceOf(SessionLogService.class, container.get(SessionLogService.class));
        assertInstanceOf(BanishmentService.class, container.get(BanishmentService.class));
        assertInstanceOf(BanIpService.class, container.get(BanIpService.class));
        assertInstanceOf(GameBanIpSynchronizer.class, container.get(GameBanIpSynchronizer.class));
        assertInstanceOf(Simulator.class, container.get(Simulator.class));
        assertInstanceOf(SavingService.class, container.get(SavingService.class));
        assertInstanceOf(DefaultSpectatorFactory.class, container.get(SpectatorFactory.class));
        assertInstanceOf(MoveFactory.class, container.get(MoveFactory.class));
        assertInstanceOf(CastFactory.class, container.get(CastFactory.class));
        assertInstanceOf(CloseCombatFactory.class, container.get(CloseCombatFactory.class));
        assertInstanceOf(BaseCriticalityStrategy.class, container.get(CriticalityStrategy.class));
        assertInstanceOf(FightActionsFactoryRegistry.class, container.get(FightActionsFactoryRegistry.class));
        assertInstanceOf(FightService.FightFactory.class, container.get(FightService.FightFactory.class));

        assertSame(
            container.get(ListenerAggregate.class),
            container.get(fr.quatrevieux.araknemu.core.event.Dispatcher.class)
        );
    }
}
