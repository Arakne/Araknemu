package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.Araknemu;
import fr.quatrevieux.araknemu.core.di.ContainerConfigurator;
import fr.quatrevieux.araknemu.core.di.ContainerModule;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.living.constraint.player.PlayerConstraints;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.data.living.repository.environment.SubAreaRepository;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.data.world.repository.SpellTemplateRepository;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerExperienceRepository;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTriggerRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcTemplateRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.QuestionRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.ResponseActionRepository;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemSetRepository;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTypeRepository;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterGroupDataRepository;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterGroupPositionRepository;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterTemplateRepository;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.CharactersService;
import fr.quatrevieux.araknemu.game.account.TokenService;
import fr.quatrevieux.araknemu.game.account.generator.CamelizeName;
import fr.quatrevieux.araknemu.game.account.generator.NameCheckerGenerator;
import fr.quatrevieux.araknemu.game.account.generator.NameGenerator;
import fr.quatrevieux.araknemu.game.account.generator.SimpleNameGenerator;
import fr.quatrevieux.araknemu.game.admin.AdminService;
import fr.quatrevieux.araknemu.game.admin.account.AccountContextResolver;
import fr.quatrevieux.araknemu.game.admin.debug.DebugContextResolver;
import fr.quatrevieux.araknemu.game.admin.player.PlayerContextResolver;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.chat.ChatService;
import fr.quatrevieux.araknemu.game.chat.channel.*;
import fr.quatrevieux.araknemu.game.connector.ConnectorService;
import fr.quatrevieux.araknemu.game.connector.RealmConnector;
import fr.quatrevieux.araknemu.game.exploration.ExplorationService;
import fr.quatrevieux.araknemu.game.exploration.area.AreaService;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionFactory;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ExplorationActionRegistry;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.challenge.ChallengeActionsFactories;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.fight.FightActionsFactories;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.MoveFactory;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator.ValidateOverweight;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator.ValidateRestrictedDirections;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator.ValidateWalkable;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
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
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.parameter.GetterResolver;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.parameter.ParametersResolver;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.parameter.VariableResolver;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilderFactory;
import fr.quatrevieux.araknemu.game.fight.builder.PvmBuilderFactory;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.AddExperience;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.SetDead;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.SynchronizeLife;
import fr.quatrevieux.araknemu.game.fight.ending.reward.generator.PvmRewardsGenerator;
import fr.quatrevieux.araknemu.game.fight.fighter.DefaultFighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.module.CommonEffectsModule;
import fr.quatrevieux.araknemu.game.fight.module.LaunchedSpellsModule;
import fr.quatrevieux.araknemu.game.fight.module.RaulebaqueModule;
import fr.quatrevieux.araknemu.game.fight.module.StatesModule;
import fr.quatrevieux.araknemu.game.fight.type.PvmType;
import fr.quatrevieux.araknemu.game.handler.loader.*;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.SuperType;
import fr.quatrevieux.araknemu.game.item.effect.mapping.*;
import fr.quatrevieux.araknemu.game.item.factory.*;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.monster.environment.MonsterEnvironmentService;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroupFactory;
import fr.quatrevieux.araknemu.game.monster.group.generator.FixedMonsterListGenerator;
import fr.quatrevieux.araknemu.game.monster.group.generator.MonsterListGeneratorSwitch;
import fr.quatrevieux.araknemu.game.monster.group.generator.RandomMonsterListGenerator;
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
import fr.quatrevieux.araknemu.network.game.GameSessionHandler;
import fr.quatrevieux.araknemu.network.game.in.GameParserLoader;
import fr.quatrevieux.araknemu.network.in.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Module for game service
 */
final public class GameModule implements ContainerModule {
    final private Araknemu app;

    public GameModule(Araknemu app) {
        this.app = app;
    }

    @Override
    public void configure(ContainerConfigurator configurator) {
        configurator.factory(
            Logger.class,
            container -> LoggerFactory.getLogger(GameService.class)
        );

        configurator.factory(
            GameService.class,
            container -> new GameService(
                container.get(GameConfiguration.class),
                container.get(RealmConnector.class),
                container.get(Server.class),
                container.get(Logger.class),
                container.get(ListenerAggregate.class),

                // Preload
                Arrays.asList(
                    container.get(DialogService.class),
                    container.get(NpcService.class),
                    container.get(MonsterEnvironmentService.class),
                    container.get(AreaService.class),
                    container.get(MapTriggerService.class),
                    container.get(MonsterService.class),
                    container.get(ExplorationMapService.class),
                    container.get(ItemService.class),
                    container.get(SpellService.class),
                    container.get(PlayerRaceService.class),
                    container.get(PlayerExperienceService.class)
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
                    container.get(MonsterEnvironmentService.class)
                )
            )
        );

        configurator.factory(
            GameConfiguration.class,
            container -> app.configuration().module(GameConfiguration.class)
        );

        configurator.factory(
            Server.class,
            container -> new NettyServer(
                container.get(SessionHandler.class),
                container.get(GameConfiguration.class).port()
            )
        );

        configurator.factory(
            SessionHandler.class,
            container -> new LoggingSessionHandler(
                new GameSessionHandler(
                    container.get(Dispatcher.class),
                    container.get(PacketParser.class)
                ),
                container.get(Logger.class)
            )
        );

        configurator.factory(
            Dispatcher.class,
            container -> new DefaultDispatcher(
                new AggregateLoader(
                    new CommonLoader(),
                    new LoggedLoader(),
                    new PlayingLoader(),
                    new ExploringLoader(),
                    new AdminLoader(),
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
                        new GameParserLoader()
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

    private void configureServices(ContainerConfigurator configurator)
    {
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
                container.get(fr.quatrevieux.araknemu.core.event.Dispatcher.class)
            )
        );

        configurator.persist(
            ExplorationMapService.class,
            container -> new ExplorationMapService(
                container.get(MapTemplateRepository.class),
                container.get(FightService.class),
                container.get(fr.quatrevieux.araknemu.core.event.Dispatcher.class),
                // Use proxy to fix circular reference between ExplorationMapService and MapTriggerService
                (map, cells) -> container.get(CellLoader.class).load(map, cells)
            )
        );

        configurator.persist(
            CellLoader.class,
            container -> new CellLoaderAggregate(new CellLoader[] {
                new TriggerLoader(container.get(MapTriggerService.class))
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
                    new ValidateRestrictedDirections()
                ),
                new ChallengeActionsFactories(container.get(FightService.class)),
                new FightActionsFactories(
                    container.get(FightService.class),
                    container.get(FighterFactory.class)
                )
            )
        );

        configurator.persist(
            ChatService.class,
            container -> new ChatService(
                container.get(GameConfiguration.class).chat(),
                new Channel[] {
                    new MapChannel(),
                    new GlobalChannel(
                        ChannelType.INCARNAM,
                        container.get(PlayerService.class)
                    ),
                    new FloodGuardChannel(
                        new GlobalChannel(
                            ChannelType.TRADE,
                            container.get(PlayerService.class)
                        ),
                        container.get(GameConfiguration.class).chat()
                    ),
                    new FloodGuardChannel(
                        new GlobalChannel(
                            ChannelType.RECRUITMENT,
                            container.get(PlayerService.class)
                        ),
                        container.get(GameConfiguration.class).chat()
                    ),
                    new GlobalChannel(
                        ChannelType.ADMIN,
                        player -> player.account().isMaster(),
                        container.get(PlayerService.class)
                    ),
                    new NullChannel(ChannelType.MEETIC),
                    new PrivateChannel(
                        container.get(PlayerService.class)
                    ),
                    new FightTeamChannel()
                }
            )
        );

        configurator.persist(
            AreaService.class,
            container -> new AreaService(
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
            container -> new DefaultListenerAggregate()
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
            AdminService.class,
            container -> new AdminService(
                Arrays.asList(
                    container.get(PlayerContextResolver.class),
                    container.get(AccountContextResolver.class),
                    container.get(DebugContextResolver.class)
                )
            )
        );

        configurator.persist(
            ItemService.class,
            container -> new ItemService(
                container.get(ItemTemplateRepository.class),
                container.get(ItemFactory.class),
                container.get(ItemSetRepository.class),
                container.get(ItemTypeRepository.class),
                container.get(EffectMappers.class)
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
            FightService.class,
            container -> new FightService(
                container.get(MapTemplateRepository.class),
                container.get(fr.quatrevieux.araknemu.core.event.Dispatcher.class),
                Arrays.asList(
                    new ChallengeBuilderFactory(container.get(FighterFactory.class)),
                    new PvmBuilderFactory(
                        container.get(FighterFactory.class),
                        container.get(PvmType.class)
                    )
                ),
                Arrays.asList(
                    CommonEffectsModule::new,
                    StatesModule::new,
                    RaulebaqueModule::new,
                    LaunchedSpellsModule::new
                )
            )
        );

        configurator.persist(
            NpcService.class,
            container -> new NpcService(
                container.get(DialogService.class),
                container.get(NpcTemplateRepository.class),
                container.get(NpcRepository.class)
            )
        );

        configurator.persist(
            DialogService.class,
            container -> new DialogService(
                container.get(QuestionRepository.class),
                container.get(ResponseActionRepository.class),
                new fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.ActionFactory[] {},
                container.get(ParametersResolver.class),
                container.get(Logger.class)
            )
        );

        configurator.persist(
            MonsterService.class,
            container -> new MonsterService(
                container.get(SpellService.class),
                container.get(MonsterTemplateRepository.class)
            )
        );

        configurator.persist(
            MonsterEnvironmentService.class,
            container -> new MonsterEnvironmentService(
                container.get(FightService.class),
                container.get(MonsterGroupFactory.class),
                container.get(MonsterGroupPositionRepository.class),
                container.get(MonsterGroupDataRepository.class)
            )
        );

        configurator.persist(
            ParametersResolver.class,
            container -> new ParametersResolver(
                new VariableResolver[] {
                    new GetterResolver("name", session -> session.player().name()),
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

        configurator.factory(
            PlayerContextResolver.class,
            container -> new PlayerContextResolver(
                container.get(PlayerService.class),
                container.get(AccountContextResolver.class),
                container.get(ItemService.class)
            )
        );

        configurator.persist(
            AccountContextResolver.class,
            container -> new AccountContextResolver(
                container.get(AccountService.class),
                container.get(AccountRepository.class)
            )
        );

        configurator.persist(
            DebugContextResolver.class,
            DebugContextResolver::new
        );

        configurator.persist(
            EffectMappers.class,
            container -> new EffectMappers(
                new EffectToSpecialMapping(),
                new EffectToWeaponMapping(),
                new EffectToCharacteristicMapping(),
                new EffectToUseMapping(
                    container.get(SpellService.class)
                )
            )
        );

        configurator.persist(
            ItemFactory.class,
            container -> new DefaultItemFactory(
                new ResourceFactory(container.get(EffectMappers.class)),
                new UsableFactory(container.get(EffectMappers.class)),
                new WeaponFactory(
                    container.get(EffectMappers.class),
                    container.get(SpellEffectService.class)
                ),
                new WearableFactory(SuperType.AMULET, container.get(EffectMappers.class)),
                new WearableFactory(SuperType.RING, container.get(EffectMappers.class)),
                new WearableFactory(SuperType.BELT, container.get(EffectMappers.class)),
                new WearableFactory(SuperType.BOOT, container.get(EffectMappers.class)),
                new WearableFactory(SuperType.SHIELD, container.get(EffectMappers.class)),
                new WearableFactory(SuperType.HELMET, container.get(EffectMappers.class)),
                new WearableFactory(SuperType.MANTLE, container.get(EffectMappers.class)),
                new WearableFactory(SuperType.DOFUS, container.get(EffectMappers.class))
            )
        );

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
                    Arrays.asList(new AddExperience(), new SynchronizeLife()),
                    Arrays.asList(new SetDead())
                )
            )
        );
    }
}
