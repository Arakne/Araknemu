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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.Araknemu;
import fr.quatrevieux.araknemu.common.account.banishment.BanIpService;
import fr.quatrevieux.araknemu.common.account.banishment.BanishmentService;
import fr.quatrevieux.araknemu.common.account.banishment.network.BanIpCheck;
import fr.quatrevieux.araknemu.common.session.SessionLogService;
import fr.quatrevieux.araknemu.core.di.ContainerConfigurator;
import fr.quatrevieux.araknemu.core.di.ContainerModule;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.core.network.Server;
import fr.quatrevieux.araknemu.core.network.netty.NettyServer;
import fr.quatrevieux.araknemu.core.network.parser.AggregatePacketParser;
import fr.quatrevieux.araknemu.core.network.parser.AggregateParserLoader;
import fr.quatrevieux.araknemu.core.network.parser.DefaultDispatcher;
import fr.quatrevieux.araknemu.core.network.parser.Dispatcher;
import fr.quatrevieux.araknemu.core.network.parser.PacketParser;
import fr.quatrevieux.araknemu.core.network.parser.ParserLoader;
import fr.quatrevieux.araknemu.core.network.session.SessionConfigurator;
import fr.quatrevieux.araknemu.core.network.session.SessionFactory;
import fr.quatrevieux.araknemu.core.network.session.extension.RateLimiter;
import fr.quatrevieux.araknemu.core.network.session.extension.SessionLogger;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.living.constraint.player.PlayerConstraints;
import fr.quatrevieux.araknemu.data.living.repository.BanIpRepository;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountBankRepository;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.data.living.repository.account.BanishmentRepository;
import fr.quatrevieux.araknemu.data.living.repository.account.BankItemRepository;
import fr.quatrevieux.araknemu.data.living.repository.account.ConnectionLogRepository;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.data.world.repository.SpellTemplateRepository;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerExperienceRepository;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTriggerRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.area.AreaRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.area.SubAreaRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcExchangeRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcTemplateRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.QuestionRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.ResponseActionRepository;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemSetRepository;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTypeRepository;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterGroupDataRepository;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterGroupPositionRepository;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterRewardItemRepository;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterRewardRepository;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterTemplateRepository;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.CharactersService;
import fr.quatrevieux.araknemu.game.account.TokenService;
import fr.quatrevieux.araknemu.game.account.bank.BankService;
import fr.quatrevieux.araknemu.game.account.generator.CamelizeName;
import fr.quatrevieux.araknemu.game.account.generator.NameCheckerGenerator;
import fr.quatrevieux.araknemu.game.account.generator.NameGenerator;
import fr.quatrevieux.araknemu.game.account.generator.SimpleNameGenerator;
import fr.quatrevieux.araknemu.game.activity.ActivityService;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.chat.ChatService;
import fr.quatrevieux.araknemu.game.chat.channel.Channel;
import fr.quatrevieux.araknemu.game.chat.channel.FightChannel;
import fr.quatrevieux.araknemu.game.chat.channel.FightSpectatorChannel;
import fr.quatrevieux.araknemu.game.chat.channel.FightTeamChannel;
import fr.quatrevieux.araknemu.game.chat.channel.FloodGuardChannel;
import fr.quatrevieux.araknemu.game.chat.channel.GlobalChannel;
import fr.quatrevieux.araknemu.game.chat.channel.MapChannel;
import fr.quatrevieux.araknemu.game.chat.channel.NullChannel;
import fr.quatrevieux.araknemu.game.chat.channel.PrivateChannel;
import fr.quatrevieux.araknemu.game.connector.ConnectorService;
import fr.quatrevieux.araknemu.game.connector.RealmConnector;
import fr.quatrevieux.araknemu.game.exploration.ExplorationService;
import fr.quatrevieux.araknemu.game.exploration.area.AreaService;
import fr.quatrevieux.araknemu.game.exploration.exchange.DefaultExchangeFactory;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeFactory;
import fr.quatrevieux.araknemu.game.exploration.exchange.npc.NpcExchangeFactories;
import fr.quatrevieux.araknemu.game.exploration.exchange.player.PlayerExchangeFactories;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionFactory;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ExplorationActionRegistry;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.challenge.ChallengeActionsFactories;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.fight.FightActionsFactories;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.MoveFactory;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator.StopOnTrigger;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator.ValidateOverweight;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator.ValidateRestrictedDirections;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator.ValidateWalkable;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.GeolocationService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.CellLoader;
import fr.quatrevieux.araknemu.game.exploration.map.cell.CellLoaderAggregate;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.MapTriggerService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.TriggerLoader;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.ActionFactoryRegistry;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.CellActionFactory;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.teleport.Teleport;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.teleport.TeleportFactory;
import fr.quatrevieux.araknemu.game.exploration.npc.NpcService;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.DialogService;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.exchange.OpenBank;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.object.RemoveObject;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.teleport.GoToAstrub;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.parameter.BankCostResolver;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.parameter.GetterResolver;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.parameter.ParametersResolver;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.parameter.VariableResolver;
import fr.quatrevieux.araknemu.game.exploration.npc.exchange.NpcExchangeService;
import fr.quatrevieux.araknemu.game.exploration.npc.store.NpcStoreService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.ai.factory.AiFactory;
import fr.quatrevieux.araknemu.game.fight.ai.factory.ChainAiFactory;
import fr.quatrevieux.araknemu.game.fight.ai.factory.DoubleAiFactory;
import fr.quatrevieux.araknemu.game.fight.ai.factory.MonsterAiFactory;
import fr.quatrevieux.araknemu.game.fight.ai.factory.type.Aggressive;
import fr.quatrevieux.araknemu.game.fight.ai.factory.type.Blocking;
import fr.quatrevieux.araknemu.game.fight.ai.factory.type.Fixed;
import fr.quatrevieux.araknemu.game.fight.ai.factory.type.Runaway;
import fr.quatrevieux.araknemu.game.fight.ai.factory.type.Support;
import fr.quatrevieux.araknemu.game.fight.ai.factory.type.Tactical;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.AlterActionPointsSimulator;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.AlterCharacteristicSimulator;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.ArmorSimulator;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.DamageSimulator;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.HealSimulator;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.PunishmentSimulator;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.RemovePointsSimulator;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.SetStateSimulator;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.StealLifeSimulator;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilderFactory;
import fr.quatrevieux.araknemu.game.fight.builder.PvmBuilderFactory;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.spell.SpellConstraintsValidator;
import fr.quatrevieux.araknemu.game.fight.castable.weapon.WeaponConstraintsValidator;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.action.AddExperience;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.action.AddItems;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.action.AddKamas;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.action.ReturnToSavePosition;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.action.SetDead;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.action.SynchronizeLife;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.pvm.PvmRewardsGenerator;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.pvm.provider.PvmEndFightActionProvider;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.pvm.provider.PvmItemDropProvider;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.pvm.provider.PvmKamasProvider;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.pvm.provider.PvmXpProvider;
import fr.quatrevieux.araknemu.game.fight.fighter.DefaultFighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.module.AiModule;
import fr.quatrevieux.araknemu.game.fight.module.CommonEffectsModule;
import fr.quatrevieux.araknemu.game.fight.module.IndirectSpellApplyEffectsModule;
import fr.quatrevieux.araknemu.game.fight.module.LaunchedSpellsModule;
import fr.quatrevieux.araknemu.game.fight.module.MonsterInvocationModule;
import fr.quatrevieux.araknemu.game.fight.module.RaulebaqueModule;
import fr.quatrevieux.araknemu.game.fight.module.StatesModule;
import fr.quatrevieux.araknemu.game.fight.spectator.DefaultSpectatorFactory;
import fr.quatrevieux.araknemu.game.fight.spectator.SpectatorFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.CastFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.closeCombat.CloseCombatFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.FightActionsFactoryRegistry;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.validators.FightPathValidator;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.validators.StopOnBattlefieldObjectValidator;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.validators.StopOnEnemyValidator;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.validators.TackleValidator;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.BaseCriticalityStrategy;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.CriticalityStrategy;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.game.fight.type.PvmType;
import fr.quatrevieux.araknemu.game.handler.loader.AdminLoader;
import fr.quatrevieux.araknemu.game.handler.loader.AggregateLoader;
import fr.quatrevieux.araknemu.game.handler.loader.CommonLoader;
import fr.quatrevieux.araknemu.game.handler.loader.ExploringLoader;
import fr.quatrevieux.araknemu.game.handler.loader.ExploringOrFightingLoader;
import fr.quatrevieux.araknemu.game.handler.loader.FightingLoader;
import fr.quatrevieux.araknemu.game.handler.loader.LoggedLoader;
import fr.quatrevieux.araknemu.game.handler.loader.PlayingLoader;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.SuperType;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToCharacteristicMapping;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToSpecialMapping;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToUseMapping;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToWeaponMapping;
import fr.quatrevieux.araknemu.game.item.factory.DefaultItemFactory;
import fr.quatrevieux.araknemu.game.item.factory.ItemFactory;
import fr.quatrevieux.araknemu.game.item.factory.ResourceFactory;
import fr.quatrevieux.araknemu.game.item.factory.UsableFactory;
import fr.quatrevieux.araknemu.game.item.factory.WeaponFactory;
import fr.quatrevieux.araknemu.game.item.factory.WearableFactory;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.monster.environment.MonsterEnvironmentService;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroupFactory;
import fr.quatrevieux.araknemu.game.monster.group.generator.FixedMonsterListGenerator;
import fr.quatrevieux.araknemu.game.monster.group.generator.MonsterListGeneratorSwitch;
import fr.quatrevieux.araknemu.game.monster.group.generator.RandomMonsterListGenerator;
import fr.quatrevieux.araknemu.game.monster.reward.MonsterRewardService;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.game.player.experience.PlayerExperienceService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryService;
import fr.quatrevieux.araknemu.game.player.race.PlayerRaceService;
import fr.quatrevieux.araknemu.game.player.spell.SpellBookService;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffectService;
import fr.quatrevieux.araknemu.network.game.GameExceptionConfigurator;
import fr.quatrevieux.araknemu.network.game.GamePacketConfigurator;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.GameParserLoader;
import fr.quatrevieux.araknemu.network.in.CommonParserLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * Module for game service
 */
public final class GameModule implements ContainerModule {
    private final Araknemu app;

    public GameModule(Araknemu app) {
        this.app = app;
    }

    @Override
    public void configure(ContainerConfigurator configurator) {
        configurator.factory(
            Logger.class,
            container -> LogManager.getLogger(GameService.class)
        );

        configurator.persist(
            GameService.class,
            container -> new GameService(
                container.get(GameConfiguration.class),
                container.get(RealmConnector.class),
                container.get(Server.class),
                container.get(Logger.class),
                container.get(ListenerAggregate.class),

                // Preload
                Arrays.asList(
                    container.get(ItemService.class),
                    container.get(SpellService.class),

                    container.get(DialogService.class),
                    container.get(NpcExchangeService.class),
                    container.get(NpcService.class),

                    container.get(MonsterRewardService.class),
                    container.get(MonsterService.class),
                    container.get(MonsterEnvironmentService.class),

                    container.get(AreaService.class),
                    container.get(MapTriggerService.class),
                    container.get(ExplorationMapService.class),

                    container.get(PlayerRaceService.class),
                    container.get(PlayerExperienceService.class),

                    container.get(GameBanIpSynchronizer.class),
                    container.get(SavingService.class)
                ),

                // Subscribers
                Arrays.asList(
                    container.get(AreaService.class),
                    container.get(ExplorationMapService.class),
                    container.get(MapTriggerService.class),
                    container.get(ChatService.class),
                    container.get(InventoryService.class),
                    container.get(SpellBookService.class),
                    container.get(PlayerExperienceService.class),
                    container.get(FightService.class),
                    container.get(ExplorationService.class),
                    container.get(NpcService.class),
                    container.get(MonsterEnvironmentService.class),
                    container.get(NpcExchangeService.class),
                    container.get(ActivityService.class),
                    container.get(PlayerService.class),
                    container.get(AccountService.class),
                    container.get(ShutdownService.class),
                    container.get(GameBanIpSynchronizer.class),
                    container.get(SavingService.class)
                )
            )
        );

        configurator.factory(
            GameConfiguration.class,
            container -> app.configuration().module(GameConfiguration.MODULE)
        );

        configurator.factory(
            Server.class,
            container -> new NettyServer(
                container.get(SessionFactory.class),
                container.get(GameConfiguration.class).port(),
                container.get(GameConfiguration.class).inactivityTime()
            )
        );

        configurator.factory(
            SessionFactory.class,
            container -> new SessionConfigurator<>(GameSession::new)
                .add(new BanIpCheck<>(container.get(BanIpService.class)))
                .add(new RateLimiter.Configurator<>(container.get(GameConfiguration.class).packetRateLimit()))
                .add(new SessionLogger.Configurator<>(container.get(Logger.class)))
                .add(new GameExceptionConfigurator(container.get(Logger.class)))
                .add(new GamePacketConfigurator(
                    container.get(Dispatcher.class),
                    container.get(PacketParser.class)
                ))
        );

        configurator.factory(
            Dispatcher.class,
            container -> new DefaultDispatcher(
                new AggregateLoader(
                    new CommonLoader(),
                    new LoggedLoader(),
                    new PlayingLoader(),
                    new ExploringLoader(),
                    new AdminLoader(), // @todo should be defined into the AdminModule
                    new FightingLoader(),
                    new ExploringOrFightingLoader()
                ).load(container)
            )
        );

        configurator.factory(
            PacketParser.class,
            container ->  new AggregatePacketParser(
                new AggregateParserLoader(
                    new ParserLoader[]{
                        new CommonParserLoader(),
                        new GameParserLoader(),
                    }
                ).load()
            )
        );

        configurator.factory(
            PlayerConstraints.class,
            container -> new PlayerConstraints(
                container.get(PlayerRepository.class),
                container.get(GameConfiguration.class).player()
            )
        );

        configureServices(configurator);
    }

    private void configureServices(ContainerConfigurator configurator) {
        configurator.persist(
            ConnectorService.class,
            container -> new ConnectorService(
                container.get(TokenService.class),
                container.get(AccountService.class)
            )
        );

        configurator.persist(
            TokenService.class,
            container -> new TokenService()
        );

        configurator.persist(
            AccountService.class,
            container -> new AccountService(
                container.get(AccountRepository.class),
                container.get(GameConfiguration.class)
            )
        );

        configurator.persist(
            CharactersService.class,
            container -> new CharactersService(
                container.get(PlayerRepository.class),
                container.get(PlayerConstraints.class),
                container.get(PlayerRaceRepository.class),
                container.get(fr.quatrevieux.araknemu.core.event.Dispatcher.class),
                container.get(PlayerItemRepository.class)
            )
        );

        configurator.persist(
            PlayerService.class,
            container -> new PlayerService(
                container.get(PlayerRepository.class),
                container.get(GameConfiguration.class),
                container.get(fr.quatrevieux.araknemu.core.event.Dispatcher.class),
                container.get(InventoryService.class),
                container.get(PlayerRaceService.class),
                container.get(SpellBookService.class),
                container.get(PlayerExperienceService.class)
            )
        );

        configurator.persist(
            ExplorationService.class,
            container -> new ExplorationService(
                container.get(ExplorationMapService.class),
                container.get(GameConfiguration.class).player(),
                container.get(fr.quatrevieux.araknemu.core.event.Dispatcher.class)
            )
        );

        configurator.persist(
            ExplorationMapService.class,
            container -> new ExplorationMapService(
                container.get(MapTemplateRepository.class),
                container.get(FightService.class),
                container.get(AreaService.class),
                container.get(fr.quatrevieux.araknemu.core.event.Dispatcher.class),
                // Use proxy to fix circular reference between ExplorationMapService and MapTriggerService
                (map, cells) -> container.get(CellLoader.class).load(map, cells)
            )
        );

        configurator.persist(
            GeolocationService.class,
            container -> new GeolocationService(
                container.get(ExplorationMapService.class),
                container.get(AreaService.class),
                container.get(MapTemplateRepository.class)
            )
        );

        configurator.persist(
            CellLoader.class,
            container -> new CellLoaderAggregate(new CellLoader[] {
                new TriggerLoader(container.get(MapTriggerService.class)),
            })
        );

        configurator.persist(
            MapTriggerService.class,
            container -> new MapTriggerService(
                container.get(MapTriggerRepository.class),
                container.get(CellActionFactory.class)
            )
        );

        configurator.persist(
            CellActionFactory.class,
            container -> new ActionFactoryRegistry()
                .register(Teleport.ACTION_ID, new TeleportFactory(container.get(ExplorationMapService.class)))
        );

        configurator.persist(
            ActionFactory.class,
            container -> new ExplorationActionRegistry(
                new MoveFactory(
                    new ValidateWalkable(),
                    new ValidateOverweight(),
                    new ValidateRestrictedDirections(),
                    new StopOnTrigger()
                ),
                new ChallengeActionsFactories(container.get(FightService.class)),
                new FightActionsFactories(
                    container.get(FightService.class),
                    container.get(FighterFactory.class),
                    container.get(SpectatorFactory.class)
                )
            )
        );

        configurator.persist(
            ChatService.class,
            container -> new ChatService(
                container.get(GameConfiguration.class).chat(),
                new Channel[] {
                    new MapChannel(),
                    new GlobalChannel(ChannelType.INCARNAM, container.get(PlayerService.class)),
                    new FloodGuardChannel(
                        new GlobalChannel(ChannelType.TRADE, container.get(PlayerService.class)),
                        container.get(GameConfiguration.class).chat()
                    ),
                    new FloodGuardChannel(
                        new GlobalChannel(ChannelType.RECRUITMENT, container.get(PlayerService.class)),
                        container.get(GameConfiguration.class).chat()
                    ),
                    new GlobalChannel(
                        ChannelType.ADMIN,
                        player -> player.account().isMaster(),
                        container.get(PlayerService.class)
                    ),
                    new NullChannel(ChannelType.MEETIC),
                    new PrivateChannel(container.get(PlayerService.class)),
                    new FightChannel(),
                    new FightTeamChannel(),
                    new FightSpectatorChannel(ChannelType.FIGHT_TEAM),
                    new FightSpectatorChannel(ChannelType.MESSAGES),
                }
            )
        );

        configurator.persist(
            AreaService.class,
            container -> new AreaService(
                container.get(AreaRepository.class),
                container.get(SubAreaRepository.class)
            )
        );

        configurator.persist(
            InventoryService.class,
            container -> new InventoryService(
                container.get(PlayerItemRepository.class),
                container.get(ItemService.class)
            )
        );

        configurator.persist(
            ListenerAggregate.class,
            container -> new DefaultListenerAggregate(container.get(Logger.class))
        );

        configurator.factory(
            fr.quatrevieux.araknemu.core.event.Dispatcher.class,
            container -> container.get(ListenerAggregate.class)
        );

        configurator.persist(
            NameGenerator.class,
            container -> new NameCheckerGenerator(
                new CamelizeName(
                    new SimpleNameGenerator(
                        container.get(GameConfiguration.class).player()
                    )
                ),
                container.get(PlayerRepository.class),
                container.get(GameConfiguration.class)
            )
        );

        configurator.persist(
            ItemService.class,
            container -> new ItemService(
                container.get(ItemTemplateRepository.class),
                container.get(ItemFactory.class),
                container.get(ItemSetRepository.class),
                container.get(ItemTypeRepository.class),
                container.get(EffectToCharacteristicMapping.class),
                container.get(EffectToSpecialMapping.class)
            )
        );

        configurator.persist(
            SpellService.class,
            container -> new SpellService(
                container.get(SpellTemplateRepository.class),
                container.get(SpellEffectService.class)
            )
        );

        configurator.persist(
            SpellEffectService.class,
            container -> new SpellEffectService()
        );

        configurator.persist(
            PlayerRaceService.class,
            container -> new PlayerRaceService(
                container.get(PlayerRaceRepository.class),
                container.get(SpellService.class)
            )
        );

        configurator.persist(
            SpellBookService.class,
            container -> new SpellBookService(
                container.get(PlayerSpellRepository.class),
                container.get(SpellService.class),
                container.get(PlayerRaceService.class)
            )
        );

        configurator.persist(
            PlayerExperienceService.class,
            container -> new PlayerExperienceService(
                container.get(PlayerExperienceRepository.class),
                container.get(GameConfiguration.class).player()
            )
        );

        configurator.persist(
            fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveFactory.class,
            container -> new fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveFactory(new FightPathValidator[] {
                new TackleValidator(),
                new StopOnEnemyValidator(),
                new StopOnBattlefieldObjectValidator(),
            })
        );

        configurator.persist(
            CastFactory.class,
            container -> new CastFactory(
                new SpellConstraintsValidator(),
                container.get(CriticalityStrategy.class)
            )
        );

        configurator.persist(
            CloseCombatFactory.class,
            container -> new CloseCombatFactory(
                new WeaponConstraintsValidator(),
                container.get(CriticalityStrategy.class)
            )
        );

        configurator.persist(
            FightActionsFactoryRegistry.class,
            container -> new FightActionsFactoryRegistry(
                container.get(fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveFactory.class),
                container.get(CastFactory.class),
                container.get(CloseCombatFactory.class)
            )
        );

        configurator.persist(
            FightService.FightFactory.class,
            container -> (id, type, map, teams, statesFlow, executor) -> new Fight(
                id,
                type,
                map,
                teams,
                statesFlow,
                container.get(Logger.class), // @todo fight logger
                executor,
                container.get(FightActionsFactoryRegistry.class)
            )
        );

        configurator.persist(
            FightService.class,
            container -> new FightService(
                container.get(fr.quatrevieux.araknemu.core.event.Dispatcher.class),
                Arrays.asList(
                    new ChallengeBuilderFactory(
                        container.get(FighterFactory.class),
                        container.get(ChallengeType.class)
                    ),
                    new PvmBuilderFactory(
                        container.get(FighterFactory.class),
                        container.get(PvmType.class)
                    )
                ),
                Arrays.asList(
                    CommonEffectsModule::new,
                    fight -> new IndirectSpellApplyEffectsModule(fight, container.get(SpellService.class)),
                    StatesModule::new,
                    RaulebaqueModule::new,
                    LaunchedSpellsModule::new,
                    fight -> new AiModule(container.get(AiFactory.class)),
                    fight -> new MonsterInvocationModule(container.get(MonsterService.class), container.get(FighterFactory.class), fight)
                ),
                container.get(FightService.FightFactory.class),
                container.get(GameConfiguration.class).fight()
            )
        );

        configurator.persist(
            NpcService.class,
            container -> new NpcService(
                container.get(DialogService.class),
                container.get(NpcTemplateRepository.class),
                container.get(NpcRepository.class),
                Arrays.asList(
                    container.get(NpcStoreService.class),
                    container.get(NpcExchangeService.class)
                )
            )
        );

        configurator.persist(
            DialogService.class,
            container -> new DialogService(
                container.get(QuestionRepository.class),
                container.get(ResponseActionRepository.class),
                new fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.ActionFactory[] {
                    new fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.teleport.Teleport.Factory(
                        container.get(ExplorationMapService.class)
                    ),
                    new GoToAstrub.Factory(container.get(ExplorationMapService.class)),
                    new RemoveObject.Factory(),
                    new OpenBank.Factory(container.get(BankService.class)),
                },
                container.get(ParametersResolver.class),
                container.get(Logger.class)
            )
        );

        configurator.persist(
            NpcStoreService.class,
            container -> new NpcStoreService(
                container.get(ItemService.class),
                container.get(ItemTemplateRepository.class),
                container.get(GameConfiguration.class).economy()
            )
        );

        configurator.persist(
            NpcExchangeService.class,
            container -> new NpcExchangeService(
                container.get(ItemService.class),
                container.get(NpcExchangeRepository.class),
                container.get(ItemTemplateRepository.class)
            )
        );

        configurator.persist(
            MonsterService.class,
            container -> new MonsterService(
                container.get(SpellService.class),
                container.get(MonsterRewardService.class),
                container.get(MonsterTemplateRepository.class)
            )
        );

        configurator.persist(
            MonsterEnvironmentService.class,
            container -> new MonsterEnvironmentService(
                container.get(ActivityService.class),
                container.get(FightService.class),
                container.get(MonsterGroupFactory.class),
                container.get(MonsterGroupPositionRepository.class),
                container.get(MonsterGroupDataRepository.class),
                container.get(GameConfiguration.class).activity()
            )
        );

        configurator.persist(
            MonsterRewardService.class,
            container -> new MonsterRewardService(
                container.get(MonsterRewardRepository.class),
                container.get(MonsterRewardItemRepository.class)
            )
        );

        configurator.persist(
            ActivityService.class,
            container -> new ActivityService(
                container.get(GameConfiguration.class).activity(),
                container.get(Logger.class) // @todo custom logger ?
            )
        );

        configurator.persist(
            BankService.class,
            container -> new BankService(
                container.get(ItemService.class),
                container.get(AccountBankRepository.class),
                container.get(BankItemRepository.class),
                container.get(GameConfiguration.class).economy()
            )
        );

        configurator.persist(
            BanishmentService.class,
            container -> new BanishmentService<>(
                container.get(BanishmentRepository.class),
                container.get(fr.quatrevieux.araknemu.core.event.Dispatcher.class),
                ids -> container.get(AccountService.class).getByIds(ids)
            )
        );

        configurator.persist(
            BanIpService.class,
            container -> new BanIpService<>(
                container.get(BanIpRepository.class),
                container.get(fr.quatrevieux.araknemu.core.event.Dispatcher.class),
                ids -> container.get(AccountService.class).getByIds(ids)
            )
        );

        configurator.persist(
            GameBanIpSynchronizer.class,
            container -> new GameBanIpSynchronizer(
                container.get(BanIpService.class),
                () -> container.get(GameService.class).sessions(),
                container.get(Logger.class),
                container.get(GameConfiguration.class).banIpRefresh()
            )
        );

        configurator.persist(
            ParametersResolver.class,
            container -> new ParametersResolver(
                new VariableResolver[] {
                    new GetterResolver("name", session -> session.player().name()),
                    new BankCostResolver(container.get(BankService.class)),
                },
                container.get(Logger.class)
            )
        );

        configurator.persist(
            FighterFactory.class,
            container -> new DefaultFighterFactory(
                container.get(fr.quatrevieux.araknemu.core.event.Dispatcher.class)
            )
        );

        configurator.persist(SpectatorFactory.class, container -> new DefaultSpectatorFactory());

        configurator.persist(EffectToSpecialMapping.class, container -> new EffectToSpecialMapping());
        configurator.persist(EffectToWeaponMapping.class, container -> new EffectToWeaponMapping());
        configurator.persist(EffectToCharacteristicMapping.class, container -> new EffectToCharacteristicMapping());
        configurator.persist(EffectToUseMapping.class, container -> new EffectToUseMapping(
            container.get(SpellService.class)
        ));

        configurator.persist(
            ItemFactory.class,
            container -> new DefaultItemFactory(
                new ResourceFactory(container.get(EffectToSpecialMapping.class)),
                new UsableFactory(
                    container.get(EffectToUseMapping.class),
                    container.get(EffectToSpecialMapping.class)
                ),
                new WeaponFactory(
                    container.get(SpellEffectService.class),
                    container.get(EffectToWeaponMapping.class),
                    container.get(EffectToCharacteristicMapping.class),
                    container.get(EffectToSpecialMapping.class)
                ),
                new WearableFactory(SuperType.AMULET, container.get(EffectToCharacteristicMapping.class), container.get(EffectToSpecialMapping.class)),
                new WearableFactory(SuperType.RING, container.get(EffectToCharacteristicMapping.class), container.get(EffectToSpecialMapping.class)),
                new WearableFactory(SuperType.BELT, container.get(EffectToCharacteristicMapping.class), container.get(EffectToSpecialMapping.class)),
                new WearableFactory(SuperType.BOOT, container.get(EffectToCharacteristicMapping.class), container.get(EffectToSpecialMapping.class)),
                new WearableFactory(SuperType.SHIELD, container.get(EffectToCharacteristicMapping.class), container.get(EffectToSpecialMapping.class)),
                new WearableFactory(SuperType.HELMET, container.get(EffectToCharacteristicMapping.class), container.get(EffectToSpecialMapping.class)),
                new WearableFactory(SuperType.MANTLE, container.get(EffectToCharacteristicMapping.class), container.get(EffectToSpecialMapping.class)),
                new WearableFactory(SuperType.DOFUS, container.get(EffectToCharacteristicMapping.class), container.get(EffectToSpecialMapping.class))
            )
        );

        configurator.persist(CriticalityStrategy.class, container -> new BaseCriticalityStrategy());

        configurator.persist(
            MonsterGroupFactory.class,
            container -> new MonsterGroupFactory(
                new MonsterListGeneratorSwitch(
                    new RandomMonsterListGenerator(container.get(MonsterService.class)),
                    new FixedMonsterListGenerator(container.get(MonsterService.class))
                )
            )
        );

        configurator.persist(
            PvmType.class,
            container -> new PvmType(
                new PvmRewardsGenerator(
                    // Issue #192 (https://github.com/Arakne/Araknemu/issues/192) : Perform SynchronizeLife before AddExperience
                    // to ensure that level up (which trigger restore life) is performed after life synchronisation
                    Arrays.asList(new SynchronizeLife(), new AddExperience(), new AddKamas(), new AddItems(container.get(ItemService.class))),
                    Arrays.asList(new SetDead(), new ReturnToSavePosition()),
                    Arrays.asList(
                        new PvmXpProvider(container.get(GameConfiguration.class).fight().xpRate()),
                        new PvmKamasProvider(),
                        new PvmItemDropProvider(container.get(GameConfiguration.class).fight().dropRate()),
                        new PvmEndFightActionProvider()
                    )
                ),
                container.get(GameConfiguration.class).fight()
            )
        );

        configurator.persist(
            ChallengeType.class,
            container -> new ChallengeType(container.get(GameConfiguration.class).fight())
        );

        configurator.persist(
            AiFactory.class,
            container -> new ChainAiFactory(
                container.get(MonsterAiFactory.class),
                container.get(DoubleAiFactory.class)
            )
        );

        // @todo Move to "FightModule"
        configurator.persist(Simulator.class, container -> {
            final Simulator simulator = new Simulator(container.get(CriticalityStrategy.class));

            simulator.register(91, new StealLifeSimulator(Element.WATER));
            simulator.register(92, new StealLifeSimulator(Element.EARTH));
            simulator.register(93, new StealLifeSimulator(Element.AIR));
            simulator.register(94, new StealLifeSimulator(Element.FIRE));
            simulator.register(95, new StealLifeSimulator(Element.NEUTRAL));

            simulator.register(96,  new DamageSimulator(Element.WATER));
            simulator.register(97,  new DamageSimulator(Element.EARTH));
            simulator.register(98,  new DamageSimulator(Element.AIR));
            simulator.register(99,  new DamageSimulator(Element.FIRE));
            simulator.register(100, new DamageSimulator(Element.NEUTRAL));

            // AP
            simulator.register(111, new AlterActionPointsSimulator(200));
            simulator.register(120, new AlterActionPointsSimulator(200));
            simulator.register(168, new AlterActionPointsSimulator(-200));
            simulator.register(101, new RemovePointsSimulator(Characteristic.ACTION_POINT, Characteristic.RESISTANCE_ACTION_POINT, 200));

            // MP
            simulator.register(78,  new AlterCharacteristicSimulator(200));
            simulator.register(128, new AlterCharacteristicSimulator(200));
            simulator.register(169, new AlterCharacteristicSimulator(-200));
            simulator.register(127, new RemovePointsSimulator(Characteristic.MOVEMENT_POINT, Characteristic.RESISTANCE_MOVEMENT_POINT, 200));

            // Characteristics boost
            simulator.register(112, new AlterCharacteristicSimulator(10)); // damage
            simulator.register(115, new AlterCharacteristicSimulator(5)); // critical
            simulator.register(117, new AlterCharacteristicSimulator(5)); // sight
            simulator.register(118, new AlterCharacteristicSimulator()); // strength
            simulator.register(119, new AlterCharacteristicSimulator()); // agility
            simulator.register(122, new AlterCharacteristicSimulator(-5)); // Fail malus
            simulator.register(123, new AlterCharacteristicSimulator()); // luck
            simulator.register(124, new AlterCharacteristicSimulator()); // wisdom
            simulator.register(126, new AlterCharacteristicSimulator()); // intelligence
            simulator.register(138, new AlterCharacteristicSimulator(2)); // percent damage
            simulator.register(178, new AlterCharacteristicSimulator(8)); // heal
            simulator.register(182, new AlterCharacteristicSimulator(10)); // summoned creature
            simulator.register(606, new AlterCharacteristicSimulator()); // Wisdom not dispellable
            simulator.register(607, new AlterCharacteristicSimulator()); // Strength not dispellable
            simulator.register(608, new AlterCharacteristicSimulator()); // Luck not dispellable
            simulator.register(609, new AlterCharacteristicSimulator()); // Agility not dispellable
            simulator.register(611, new AlterCharacteristicSimulator()); // Intelligence not dispellable

            // Characteristics malus
            simulator.register(116, new AlterCharacteristicSimulator(-5)); // sight malus
            simulator.register(145, new AlterCharacteristicSimulator(-10)); // -damage
            simulator.register(152, new AlterCharacteristicSimulator(-1)); // -luck
            simulator.register(154, new AlterCharacteristicSimulator(-1)); // -agility
            simulator.register(155, new AlterCharacteristicSimulator(-1)); // -intelligence
            simulator.register(156, new AlterCharacteristicSimulator(-1)); // -wisdom
            simulator.register(157, new AlterCharacteristicSimulator(-1)); // -strength
            simulator.register(171, new AlterCharacteristicSimulator(-5)); // -critical
            simulator.register(179, new AlterCharacteristicSimulator(-8)); // -heal
            simulator.register(186, new AlterCharacteristicSimulator(-2)); // -percent damage

            simulator.register(105, new ArmorSimulator());
            simulator.register(265, new ArmorSimulator());

            // Heal
            simulator.register(108, new HealSimulator());

            // Misc
            simulator.register(950, new SetStateSimulator()
                .state(50, -500) // Altruiste
            );
            simulator.register(150, new PunishmentSimulator());

            return simulator;
        });

        configurator.persist(
            MonsterAiFactory.class,
            container -> {
                final MonsterAiFactory factory = new MonsterAiFactory();
                final Simulator simulator = container.get(Simulator.class);

                factory.register("AGGRESSIVE", new Aggressive(simulator));
                factory.register("RUNAWAY", new Runaway(simulator));
                factory.register("SUPPORT", new Support(simulator));
                factory.register("TACTICAL", new Tactical(simulator));
                factory.register("FIXED", new Fixed(simulator));
                factory.register("BLOCKING", new Blocking());

                return factory;
            }
        );

        configurator.persist(
            DoubleAiFactory.class,
            container -> new DoubleAiFactory(new Blocking())
        );

        configurator.persist(ExchangeFactory.class, container -> new DefaultExchangeFactory(
            new PlayerExchangeFactories(),
            new NpcExchangeFactories()
        ));

        configurator.persist(ShutdownService.class, container -> new ShutdownService(
            app,
            container.get(fr.quatrevieux.araknemu.core.event.Dispatcher.class),
            container.get(GameConfiguration.class)
        ));

        configurator.persist(SessionLogService.class, container -> new SessionLogService(
            container.get(ConnectionLogRepository.class)
        ));

        configurator.persist(SavingService.class, container -> new SavingService(
            container.get(PlayerService.class),
            container.get(GameConfiguration.class),
            container.get(fr.quatrevieux.araknemu.core.event.Dispatcher.class)
        ));
    }
}
