package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.Araknemu;
import fr.quatrevieux.araknemu.DatabaseTestCase;
import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.core.config.Configuration;
import fr.quatrevieux.araknemu.core.config.DefaultConfiguration;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import fr.quatrevieux.araknemu.core.dbal.DatabaseConfiguration;
import fr.quatrevieux.araknemu.core.dbal.DefaultDatabaseHandler;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
import fr.quatrevieux.araknemu.core.di.*;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.environment.SubArea;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.living.repository.environment.SubAreaRepository;
import fr.quatrevieux.araknemu.data.living.repository.implementation.sql.LivingRepositoriesModule;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTriggerRepository;
import fr.quatrevieux.araknemu.data.world.repository.implementation.sql.WorldRepositoriesModule;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.connector.RealmConnector;
import fr.quatrevieux.araknemu.game.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.exploration.StartExploration;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.ExplorationService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryService;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import fr.quatrevieux.araknemu.network.adapter.util.DummyChannel;
import fr.quatrevieux.araknemu.network.game.GameSession;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.ini4j.Ini;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.io.File;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.EnumSet;

public class GameBaseCase extends DatabaseTestCase {
    static public class SendingRequestStack extends IoFilterAdapter {
        final public DummyChannel channel;

        public SendingRequestStack(DummyChannel channel) {
            this.channel = channel;
        }

        @Override
        public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
            channel.getMessages().push(writeRequest.getMessage());
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
    protected DummyChannel channel;
    protected GameSession session;
    protected SendingRequestStack requestStack;
    protected IoHandler ioHandler;
    protected Araknemu app;
    protected GameDataSet dataSet;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        Configuration conf = new DefaultConfiguration(
            new IniDriver(new Ini(new File("src/test/test_config.ini")))
        );

        app = new Araknemu(
            conf,
            new DefaultDatabaseHandler(
                conf.module(DatabaseConfiguration.class)
            )
        );

        container = new ItemPoolContainer();
        container.register(new ConnectorModule());
        container.register(new GameModule(app));
        container.register(new LivingRepositoriesModule(
            app.database().get("game")
        ));
        container.register(new WorldRepositoriesModule(
            app.database().get("game")
        ));

        configuration = container.get(GameConfiguration.class);

        channel = new DummyChannel();
        session = new GameSession(channel);
        requestStack = new SendingRequestStack(channel);

        dataSet = new GameDataSet(
            container,
            new ConnectionPoolUtils(app.database().get("game"))
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
        ;
    }

    @AfterEach
    void tearDown() throws ContainerException {
        dataSet.destroy();
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
        dataSet.use(PlayerItem.class);

        if (!session.isLogged()) {
            login();
        }

        if (session.player() != null) {
            return session.player();
        }

        dataSet.pushRaces();

        MutableCharacteristics characteristics = new DefaultCharacteristics();

        characteristics.set(Characteristic.STRENGTH, 50);
        characteristics.set(Characteristic.INTELLIGENCE, 150);

        Player player = dataSet.push(new Player(-1, session.account().id(), session.account().serverId(), "Bob", Race.FECA, Sex.MALE, new Colors(123, 456, 789), 50, characteristics, new Position(10540, 200), EnumSet.allOf(ChannelType.class), 0, 0));

        if (!load) {
            session.setPlayer(
                new GamePlayer(
                    session.account(),
                    player,
                    dataSet.repository(PlayerRace.class).get(new PlayerRace(Race.FECA)),
                    session,
                    container.get(PlayerService.class),
                    container.get(InventoryService.class).load(player, session)
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

        GamePlayer player = gamePlayer();
        player.setPosition(new Position(10300, 279));

        dataSet.pushMaps();

        ExplorationPlayer explorationPlayer = container.get(ExplorationService.class).create(player);

        session.setExploration(explorationPlayer);
        explorationPlayer.dispatch(new StartExploration(explorationPlayer));

        return explorationPlayer;
    }

    public GamePlayer makeOtherPlayer() throws Exception {
        dataSet.pushRaces();
        dataSet.use(PlayerItem.class);

        Player player = dataSet.push(new Player(-1, 5, 2, "Other", Race.CRA, Sex.MALE, new Colors(-1, -1, -1), 1, new DefaultCharacteristics(), new Position(10540, 210), EnumSet.allOf(ChannelType.class), 0, 0));
        GameSession session = new GameSession(new DummyChannel());

        session.attach(new GameAccount(
            new Account(5),
            container.get(AccountService.class),
            2
        ));

        return container.get(PlayerService.class).load(
            session,
            player.id()
        );
    }

    public GamePlayer makeSimpleGamePlayer(int id) throws ContainerException {
        return makeSimpleGamePlayer(id, new GameSession(new DummyChannel()));
    }

    public GameSession makeSimpleExplorationSession(int id) throws ContainerException {
        GameSession session = new GameSession(new DummyChannel());
        GamePlayer gp = makeSimpleGamePlayer(id, session);

        ExplorationPlayer ep = new ExplorationPlayer(gp);
        session.setExploration(ep);

        return session;
    }

    public GamePlayer makeSimpleGamePlayer(int id, GameSession session) throws ContainerException {
        dataSet.use(PlayerItem.class);

        Player player = dataSet.createPlayer(id);

        GamePlayer gp = new GamePlayer(
            new GameAccount(
                new Account(id),
                container.get(AccountService.class),
                2
            ),
            player,
            new PlayerRace(Race.FECA),
            session,
            container.get(PlayerService.class),
            container.get(InventoryService.class).load(player, session)
        );

        session.setPlayer(gp);

        return gp;
    }
}
