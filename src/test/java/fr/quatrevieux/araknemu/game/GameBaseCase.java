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

import fr.quatrevieux.araknemu.Araknemu;
import fr.quatrevieux.araknemu.DatabaseTestCase;
import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.core.config.Configuration;
import fr.quatrevieux.araknemu.core.config.DefaultConfiguration;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import fr.quatrevieux.araknemu.core.dbal.DatabaseConfiguration;
import fr.quatrevieux.araknemu.core.dbal.DefaultDatabaseHandler;
import fr.quatrevieux.araknemu.core.dbal.executor.ConnectionPoolExecutor;
import fr.quatrevieux.araknemu.core.di.*;
import fr.quatrevieux.araknemu.core.network.Server;
import fr.quatrevieux.araknemu.core.network.parser.Dispatcher;
import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.session.SessionFactory;
import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import fr.quatrevieux.araknemu.core.network.util.DummyServer;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.account.AccountBank;
import fr.quatrevieux.araknemu.data.living.entity.account.BankItem;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountBankRepository;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.data.living.repository.account.BankItemRepository;
import fr.quatrevieux.araknemu.data.living.repository.implementation.sql.SqlLivingRepositoriesModule;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.SpellTemplate;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerExperience;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.data.world.entity.environment.area.Area;
import fr.quatrevieux.araknemu.data.world.entity.environment.area.SubArea;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.*;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemSet;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.data.world.entity.monster.*;
import fr.quatrevieux.araknemu.data.world.repository.SpellTemplateRepository;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerExperienceRepository;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTriggerRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.area.AreaRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.area.SubAreaRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.*;
import fr.quatrevieux.araknemu.data.world.repository.implementation.sql.SqlWorldRepositoriesModule;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemSetRepository;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTypeRepository;
import fr.quatrevieux.araknemu.data.world.repository.monster.*;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.AdminModule;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.connector.RealmConnector;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.ExplorationService;
import fr.quatrevieux.araknemu.game.exploration.event.StartExploration;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.game.player.experience.PlayerExperienceService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryService;
import fr.quatrevieux.araknemu.game.player.race.PlayerRaceService;
import fr.quatrevieux.araknemu.game.player.spell.SpellBookService;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.util.RandomUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.ini4j.Ini;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.EnumSet;

public class GameBaseCase extends DatabaseTestCase {
    static public class SendingRequestStack {
        final public DummyChannel channel;

        public SendingRequestStack(DummyChannel channel) {
            this.channel = channel;
        }

        public void assertLast(Object packet) {
            assertLast(packet.toString());
        }

        public void assertLast(String packet) {
            Assertions.assertEquals(packet, channel.getMessages().peek().toString());
        }

        public void assertCount(int count) {
            Assertions.assertEquals(count, channel.getMessages().size());
        }

        public void assertAll(Object... packets) {
            Assertions.assertArrayEquals(
                Arrays.stream(packets).map(Object::toString).toArray(),
                channel.getMessages().stream().map(Object::toString).toArray()
            );
        }

        public void assertEmpty() {
            Assertions.assertTrue(channel.getMessages().isEmpty());
        }

        public void clear() {
            channel.getMessages().clear();
        }

        public void assertContains(Class type) {
            for (Object message : channel.getMessages()) {
                if (type.isInstance(message)) {
                    return;
                }
            }

            Assertions.fail("Cannot find packet of type" + type.getSimpleName());
        }

        public void assertOne(Object packet) {
            for (Object message : channel.getMessages()) {
                if (message.toString().equals(packet.toString())) {
                    return;
                }
            }

            Assertions.fail("Cannot find packet " + packet);
        }
    }

    static public class ConnectorModule implements ContainerModule {
        @Override
        public void configure(ContainerConfigurator configurator) {
            configurator.persist(
                RealmConnector.class,
                c -> Mockito.mock(RealmConnector.class)
            );
        }
    }

    protected Container container;
    protected GameConfiguration configuration;
    protected DummyServer<GameSession> server;
    protected DummyChannel channel;
    protected GameSession session;
    protected SendingRequestStack requestStack;
    protected Araknemu app;
    protected GameDataSet dataSet;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        RandomUtil.enableTestingMode();

        Configuration conf = new DefaultConfiguration(
            new IniDriver(new Ini(new File("src/test/test_config.ini")))
        );

        app = new Araknemu(
            conf,
            new DefaultDatabaseHandler(
                conf.module(DatabaseConfiguration.class),
                LogManager.getLogger()
            )
        );

        container = new ItemPoolContainer();
        container.register(new ConnectorModule());
        container.register(new GameModule(app));
        container.register(new AdminModule());
        container.register(new SqlLivingRepositoriesModule(
            app.database().get("game")
        ));
        container.register(new SqlWorldRepositoriesModule(
            app.database().get("game")
        ));
        server = new DummyServer<>(container.get(SessionFactory.class));
        container.register(configurator -> configurator.set(Server.class, server));

        configuration = container.get(GameConfiguration.class);

        session = server.createSession();
        channel = (DummyChannel) session.channel();
        requestStack = new SendingRequestStack(channel);

        dataSet = new GameDataSet(
            container,
            new ConnectionPoolExecutor(app.database().get("game"))
        );

        dataSet
            .declare(Account.class, AccountRepository.class)
            .declare(Player.class, PlayerRepository.class)
            .declare(PlayerRace.class, PlayerRaceRepository.class)
            .declare(MapTemplate.class, MapTemplateRepository.class)
            .declare(MapTrigger.class, MapTriggerRepository.class)
            .declare(SubArea.class, SubAreaRepository.class)
            .declare(ItemTemplate.class, ItemTemplateRepository.class)
            .declare(PlayerItem.class, PlayerItemRepository.class)
            .declare(ItemSet.class, ItemSetRepository.class)
            .declare(SpellTemplate.class, SpellTemplateRepository.class)
            .declare(PlayerSpell.class, PlayerSpellRepository.class)
            .declare(PlayerExperience.class, PlayerExperienceRepository.class)
            .declare(ItemType.class, ItemTypeRepository.class)
            .declare(NpcTemplate.class, NpcTemplateRepository.class)
            .declare(Npc.class, NpcRepository.class)
            .declare(Question.class, QuestionRepository.class)
            .declare(ResponseAction.class, ResponseActionRepository.class)
            .declare(MonsterTemplate.class, MonsterTemplateRepository.class)
            .declare(MonsterGroupData.class, MonsterGroupDataRepository.class)
            .declare(MonsterGroupPosition.class, MonsterGroupPositionRepository.class)
            .declare(MonsterRewardData.class, MonsterRewardRepository.class)
            .declare(MonsterRewardItem.class, MonsterRewardItemRepository.class)
            .declare(AccountBank.class, AccountBankRepository.class)
            .declare(BankItem.class, BankItemRepository.class)
            .declare(NpcExchange.class, NpcExchangeRepository.class)
            .declare(Area.class, AreaRepository.class)
        ;

        container.get(GameService.class).subscribe();
    }

    @AfterEach
    public void tearDown() throws ContainerException {
        dataSet.destroy();
        app.database().stop();
    }

    public void assertClosed() {
        Assertions.assertFalse(channel.isAlive());
    }

    public void login() throws ContainerException {
        GameAccount account = new GameAccount(
            new Account(1, "toto", "", "bob", EnumSet.noneOf(Permission.class), "my question", "my response"),
            container.get(AccountService.class),
            configuration.id()
        );

        account.attach(session);
    }

    public GamePlayer gamePlayer() throws ContainerException, SQLException {
        return gamePlayer(false);
    }

    public GamePlayer gamePlayer(boolean load) throws ContainerException, SQLException {
        dataSet
            .use(PlayerItem.class)
            .use(PlayerSpell.class)
        ;

        if (!session.isLogged()) {
            login();
        }

        if (session.player() != null) {
            return session.player();
        }

        dataSet
            .pushSpells()
            .pushRaces()
            .pushExperience()
        ;

        container.get(PlayerExperienceService.class).preload(container.get(Logger.class));

        MutableCharacteristics characteristics = new DefaultCharacteristics();

        characteristics.set(Characteristic.STRENGTH, 50);
        characteristics.set(Characteristic.INTELLIGENCE, 150);

        Player player = dataSet.push(new Player(-1, session.account().id(), session.account().serverId(), "Bob", Race.FECA, Sex.MALE, new Colors(123, 456, 789), 50, characteristics, new Position(10540, 200), EnumSet.allOf(ChannelType.class), 0, 0, -1, 5481459, new Position(10540, 200), 15225));

        if (!load) {
            session.setPlayer(
                new GamePlayer(
                    session.account(),
                    player,
                    container.get(PlayerRaceService.class).get(Race.FECA),
                    session,
                    container.get(PlayerService.class),
                    container.get(InventoryService.class).load(player),
                    container.get(SpellBookService.class).load(session, player),
                    container.get(PlayerExperienceService.class).load(session, player)
                )
            );
        } else {
            session.setPlayer(
                container.get(PlayerService.class).load(
                    session,
                    player.id()
                )
            );
        }

        return session.player();
    }

    public ExplorationPlayer explorationPlayer() throws ContainerException, SQLException {
        if (!session.isLogged()) {
            login();
        }

        if (session.exploration() != null) {
            return session.exploration();
        }

        GamePlayer player = gamePlayer(true);
        player.setPosition(new Position(10300, 279));

        dataSet
            .pushAreas()
            .pushSubAreas()
            .pushMaps()
        ;

        ExplorationPlayer explorationPlayer = container.get(ExplorationService.class).create(player);

        session.setExploration(explorationPlayer);
        explorationPlayer.dispatch(new StartExploration(explorationPlayer));

        return explorationPlayer;
    }

    public GamePlayer makeOtherPlayer() throws Exception {
        return makeOtherPlayer(1);
    }

    public GamePlayer makeOtherPlayer(int level) throws Exception {
        dataSet
            .pushSpells()
            .pushRaces()
            .pushExperience()
            .use(PlayerItem.class)
            .use(PlayerSpell.class)
        ;

        container.get(PlayerExperienceService.class).preload(container.get(Logger.class));

        Player player = dataSet.push(new Player(-1, 5, 2, "Other", Race.CRA, Sex.MALE, new Colors(-1, -1, -1), level, new DefaultCharacteristics(), new Position(10540, 210), EnumSet.allOf(ChannelType.class), 0, 0, -1, 0, new Position(10540, 210), 0));
        GameSession session = server.createSession();

        session.attach(new GameAccount(
            new Account(5),
            container.get(AccountService.class),
            2
        ));

        GamePlayer gp =  container.get(PlayerService.class).load(
            session,
            player.id()
        );

        session.setPlayer(gp);

        return gp;
    }

    public ExplorationPlayer makeOtherExplorationPlayer() throws Exception {
        GamePlayer player = makeOtherPlayer();
        ExplorationPlayer explorationPlayer = container.get(ExplorationService.class).create(player);

        Field sessionField = GamePlayer.class.getDeclaredField("session");
        sessionField.setAccessible(true);

        GameSession.class.cast(sessionField.get(player)).setExploration(explorationPlayer);

        return explorationPlayer;
    }

    public ExplorationPlayer makeExplorationPlayer(GamePlayer player) throws Exception {
        ExplorationPlayer explorationPlayer = container.get(ExplorationService.class).create(player);

        Field sessionField = GamePlayer.class.getDeclaredField("session");
        sessionField.setAccessible(true);

        GameSession.class.cast(sessionField.get(player)).setExploration(explorationPlayer);

        return explorationPlayer;
    }

    public GamePlayer makeSimpleGamePlayer(int id) throws ContainerException, SQLException {
        GameSession session = server.createSession();
        return makeSimpleGamePlayer(id, session);
    }

    public GameSession makeSimpleExplorationSession(int id) throws ContainerException, SQLException {
        GameSession session = server.createSession();
        GamePlayer gp = makeSimpleGamePlayer(id, session);

        ExplorationPlayer ep = new ExplorationPlayer(gp);
        session.setExploration(ep);

        return session;
    }

    public GamePlayer makeSimpleGamePlayer(int id, GameSession session) throws ContainerException, SQLException {
        dataSet
            .pushSpells()
            .pushRaces()
            .pushExperience()
            .use(PlayerItem.class)
            .use(PlayerSpell.class)
        ;

        container.get(PlayerExperienceService.class).preload(container.get(Logger.class));

        Player player = dataSet.createPlayer(id);

        GamePlayer gp = new GamePlayer(
            new GameAccount(
                new Account(id),
                container.get(AccountService.class),
                2
            ),
            player,
            container.get(PlayerRaceService.class).get(Race.FECA),
            session,
            container.get(PlayerService.class),
            container.get(InventoryService.class).load(player),
            container.get(SpellBookService.class).load(session, player),
            container.get(PlayerExperienceService.class).load(session, player)
        );

        session.setPlayer(gp);

        return gp;
    }

    public void handlePacket(Packet packet) throws Exception {
        container.get(Dispatcher.class).dispatch(session, packet);
    }
}
